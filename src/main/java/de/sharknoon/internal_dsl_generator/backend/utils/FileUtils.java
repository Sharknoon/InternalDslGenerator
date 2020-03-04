package de.sharknoon.internal_dsl_generator.backend.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

public class FileUtils {

    public static Path zipDirectory(Path directoryToZip, Path zipFile) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFile))) {

            Files.walk(directoryToZip)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(directoryToZip.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        return zipFile;
    }

    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if (e != null) return handleException(e);
                Files.delete(dir);
                return CONTINUE;
            }

            private FileVisitResult handleException(final IOException e) {
                e.printStackTrace();
                return TERMINATE;
            }
        });
    }

    ;

}
