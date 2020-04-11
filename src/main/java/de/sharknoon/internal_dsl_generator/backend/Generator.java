package de.sharknoon.internal_dsl_generator.backend;

import de.etgramlich.dsl.Main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    private Generator() {
    }

    public static OutputDirectoryStep newBuilder() {
        return new Builder();
    }

    public interface OutputDirectoryStep {
        /**
         * @param outputDirectory Target directory in that the package folder and interfaces will be saved.
         * @return PackageNameStep
         */
        PackageNameStep outputDirectory(Path outputDirectory);
    }

    public interface PackageNameStep {
        /**
         * @param packageName Java package in that the interfaces will be located (a subdirectory will be created in the
         *                    output directory).
         * @return GrammarFileStep
         */
        GrammarFileStep packageName(String packageName);
    }

    public interface GrammarFileStep {
        /**
         * @param grammarFile EBNF grammar in text file to generate interfaces from.
         * @return Builder
         */
        Builder grammarFile(Path grammarFile);
    }

    public interface DOTGraphStep {
        /**
         * @param dotGraph A relative path to the output directory to create a DOT Graph. Optional.
         * @return Builder
         */
        Builder dotGraph(Path dotGraph);
    }

    public interface LanguageStep {
        /**
         * @param language The output language of this generator
         * @return Builder
         */
        Builder language(Language language);
    }

    public interface ReturnTypeStep {
        /**
         * @param returnType Determines the return type of the end method.
         * @return Builder
         */
        Builder returnType(String returnType);
    }

    public interface RunStep {
        /**
         * Runs the Generator with the specified parameters
         *
         * @throws Exception if something fails during the generation
         */
        void run() throws Exception;
    }

    public static class Builder implements
            OutputDirectoryStep,
            PackageNameStep,
            GrammarFileStep,
            DOTGraphStep,
            LanguageStep,
            ReturnTypeStep,
            RunStep {

        private Path r_outputDirectory;
        private String r_packageName;
        private Path r_grammarFile;
        private Path o_DOTGraph;
        private Language o_language;
        private String o_returnType;

        private Builder() {
        }

        @Override
        public PackageNameStep outputDirectory(Path outputDirectory) {
            r_outputDirectory = outputDirectory;
            return this;
        }

        @Override
        public GrammarFileStep packageName(String packageName) {
            r_packageName = packageName;
            return this;
        }

        @Override
        public Builder grammarFile(Path grammarFile) {
            r_grammarFile = grammarFile;
            return this;
        }

        @Override
        public Builder dotGraph(Path dotGraph) {
            o_DOTGraph = dotGraph;
            return this;
        }

        @Override
        public Builder language(Language language) {
            o_language = language;
            return this;
        }

        @Override
        public Builder returnType(String returnType) {
            o_returnType = returnType;
            return this;
        }

        @Override
        public void run() {
            //Required
            ArrayList<String> argsList = new ArrayList<>(List.of(
                    "-d", r_outputDirectory.toAbsolutePath().toString(),
                    "-p", r_packageName,
                    "-g", r_grammarFile.toAbsolutePath().toString()
            ));
            //Optional
            if (o_DOTGraph != null) {
                argsList.addAll(List.of("-s", o_DOTGraph.toString()));
            }
            if (o_language != null) {
                switch (o_language) {
                    case JAVA:
                        argsList.add("-j");
                        break;
                    case SCALA:
                        argsList.add("-c");
                        break;
                }
            }
            if (o_returnType != null) {
                argsList.addAll(List.of("-r", o_returnType));
            }
            String[] args = argsList.toArray(String[]::new);
            Main.main(args);
        }
    }

}
