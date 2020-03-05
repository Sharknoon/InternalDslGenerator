package de.sharknoon.internal_dsl_generator.backend;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import de.sharknoon.internal_dsl_generator.backend.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class GeneratorService {

    public static StreamResource generate(MemoryBuffer buffer, String packageName) throws IOException {
        //Creating directory to put the generated files in
        Path genDirectory = Files.createTempDirectory("generated");
        //Creating a file to save the uploaded grammar in
        Path grammarFile = Files.createTempFile("grammar", ".tmp");
        //Save the uploaded grammar in that file
        writeBufferToFile(buffer, grammarFile);

        //Run the Generation itself
        Generator.run(genDirectory, packageName, grammarFile);

        //Create the new resulting Zip stream from the directory with the generated files
        ByteArrayOutputStream zip = FileUtils.zipDirectory(genDirectory);

        //Delete all the unnecessary Files and Directories
        FileUtils.deleteFileOrFolder(genDirectory);
        FileUtils.deleteFileOrFolder(grammarFile);

        //Creating Vaadin StreamResource to download the zip file in the ui
        return new StreamResource(
                setZipFileEnding(buffer.getFileName()),
                () -> new ByteArrayInputStream(zip.toByteArray())
        );
    }


    private static void writeBufferToFile(MemoryBuffer buffer, Path file) throws IOException {
        Files.copy(
                buffer.getInputStream(),
                file,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    private static String setZipFileEnding(String fileName) {
        int i = fileName.lastIndexOf('.');
        return fileName.substring(0, i) + ".zip";
    }

}
