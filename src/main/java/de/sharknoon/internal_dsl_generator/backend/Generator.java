package de.sharknoon.internal_dsl_generator.backend;

import de.etgramlich.Main;

import java.nio.file.Path;

public class Generator {

    static void run(Path outputDirectoryPath, String packageName, Path grammarFile) {
        Main.main(new String[]{
                "-d", outputDirectoryPath.toAbsolutePath().toString(),
                "-p", packageName,
                "-g", grammarFile.toAbsolutePath().toString()
        });
    }

}
