package de.sharknoon.internal_dsl_generator.backend

import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import de.etgramlich.dsl.Main
import de.sharknoon.internal_dsl_generator.utils.deleteRecursive
import de.sharknoon.internal_dsl_generator.utils.writeToFile
import de.sharknoon.internal_dsl_generator.utils.toZipFile
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path

@Service
class GeneratorService {

    fun generate(buffer: MemoryBuffer, packageName: String): StreamResource {
        //Creating directory to put the generated files in
        val genDirectory = Files.createTempDirectory("generated")
        //Creating a file to save the uploaded grammar in
        val grammarFile = Files.createTempFile("grammar", ".tmp")
        //Save the uploaded grammar in that file
        buffer.writeToFile(grammarFile)

        //Run the Generation itself
        runInternalDSLGenerator(genDirectory, packageName, grammarFile)

        //Create the new resulting Zip stream from the directory with the generated files
        val zip = genDirectory.toZipFile()

        //Delete all the unnecessary Files and Directories
        genDirectory.deleteRecursive()
        grammarFile.deleteRecursive()

        //Creating Vaadin StreamResource to download the zip file in the ui
        return StreamResource(
                buffer.fileName.replaceAfterLast('.', "zip"),
                InputStreamFactory { ByteArrayInputStream(zip.toByteArray()) }
        )
    }

}

fun runInternalDSLGenerator(outputDirectoryPath: Path, packageName: String, grammarFile: Path) {
    Main.main(arrayOf(
            "-d", outputDirectoryPath.toAbsolutePath().toString(),
            "-p", packageName,
            "-g", grammarFile.toAbsolutePath().toString()
    ))
}