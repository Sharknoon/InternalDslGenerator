package de.sharknoon.internal_dsl_generator.utils

import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


fun MemoryBuffer.writeToFile(file: Path) {
    Files.copy(
            this.inputStream,
            file,
            StandardCopyOption.REPLACE_EXISTING
    )
}

fun Path.toZipFile(): ByteArrayOutputStream {
    val out = ByteArrayOutputStream()
    ZipOutputStream(out).use { zs ->
        Files.walk(this)
                .filter { !Files.isDirectory(it) }
                .forEach { path ->
                    val zipEntry = ZipEntry(this.relativize(path).toString())
                    try {
                        zs.putNextEntry(zipEntry)
                        Files.copy(path, zs)
                        zs.closeEntry()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
    }
    return out
}

fun Path.deleteRecursive() {
    Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {

        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
            Files.delete(file)
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path, e: IOException): FileVisitResult {
            handleException(e)
            return FileVisitResult.TERMINATE
        }

        override fun postVisitDirectory(dir: Path, e: IOException?): FileVisitResult {
            if (e != null) {
                handleException(e)
                return FileVisitResult.TERMINATE
            }
            Files.delete(dir)
            return FileVisitResult.CONTINUE
        }

        private fun handleException(e: IOException) {
            e.printStackTrace()
        }
    })
}

