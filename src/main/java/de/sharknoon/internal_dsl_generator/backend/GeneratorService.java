package de.sharknoon.internal_dsl_generator.backend;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import de.sharknoon.internal_dsl_generator.backend.utils.FileUtils;
import de.sharknoon.internal_dsl_generator.views.generator.GeneratorState;
import org.springframework.stereotype.Service;

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

        //Create the new resulting Zip file from the directory with the generated files
        Path zip = FileUtils.zipDirectory(
                genDirectory,
                Paths.get(setZipFileEnding(buffer.getFileName()))
        );

        //Delete all the unnecessary Files and Directories
        FileUtils.deleteFileOrFolder(genDirectory);
        FileUtils.deleteFileOrFolder(grammarFile);

        //Creating Vaadin StreamResource to download the zip file in the ui
        InputStream zipStream = Files.newInputStream(zip);

        return new StreamResource(
                zip.getFileName().toString(),
                () -> zipStream
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
