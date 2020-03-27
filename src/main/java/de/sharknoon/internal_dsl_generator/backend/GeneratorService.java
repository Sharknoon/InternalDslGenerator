package de.sharknoon.internal_dsl_generator.backend;

import com.vaadin.flow.server.StreamResource;
import de.sharknoon.internal_dsl_generator.backend.utils.FileUtils;
import de.sharknoon.internal_dsl_generator.views.generator.Project;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class GeneratorService implements Serializable {

    public StreamResource generate(Project project, Consumer<String> graph) throws IOException {
        Objects.requireNonNull(project);
        //Creating directory to put the generated files in
        Path genDirectory = Files.createTempDirectory("generated");
        //Creating a file to save the uploaded grammar in
        Path grammarFile = Files.createTempFile("grammar", ".tmp");
        //Save the uploaded grammar in that file
        Files.writeString(grammarFile, project.getGrammar());

        //Run the Generation itself
        Generator.Builder builder = Generator.newBuilder()
                .outputDirectory(genDirectory)
                .packageName(project.getPackageName())
                .grammarFile(grammarFile);
        if (project.isIncludeDOTGraph()) {
            builder.dotGraph(Paths.get("graph"));
        }
        builder.run();

        if (project.isIncludeDOTGraph()) {
            //Get the graph string for the consumer
            String graphString = Files.readString(genDirectory.resolve("graph.gv"));
            graph.accept(graphString);
        }

        //Create the new resulting Zip stream from the directory with the generated files
        ByteArrayOutputStream zip = FileUtils.zipDirectory(genDirectory);

        //Delete all the unnecessary Files and Directories
        FileUtils.deleteFileOrFolder(genDirectory);
        FileUtils.deleteFileOrFolder(grammarFile);

        //Creating Vaadin StreamResource to download the zip file in the ui
        return new StreamResource(
                setZipFileEnding(project.getGrammarName()),
                () -> new ByteArrayInputStream(zip.toByteArray())
        );
    }

    private String setZipFileEnding(String fileName) {
        String lhs;
        int i;
        if (fileName == null || fileName.isEmpty()) {
            lhs = "dsl";
        } else if ((i = fileName.lastIndexOf('.')) < 0) {
            lhs = fileName;
        } else {
            lhs = fileName.substring(0, i);
        }
        return lhs + ".zip";
    }

}
