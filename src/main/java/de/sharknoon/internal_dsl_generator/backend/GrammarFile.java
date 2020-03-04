package de.sharknoon.internal_dsl_generator.backend;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

public final class GrammarFile {

    private final String filename;
    private final Long contentLength;
    private final MemoryBuffer memoryBuffer;

    public GrammarFile(String filename, Long contentLength, MemoryBuffer memoryBuffer) {
        this.filename = filename;
        this.contentLength = contentLength;
        this.memoryBuffer = memoryBuffer;
    }

    public String getFilename() {
        return filename;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public MemoryBuffer getMemoryBuffer() {
        return memoryBuffer;
    }

    @Override
    public String toString() {
        return filename + "(" + contentLength + " Bytes)";
    }

    @Override
    public int hashCode() {
        if (memoryBuffer == null) {
            return super.hashCode();
        } else {
            return memoryBuffer.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || memoryBuffer == null) {
            return false;
        }
        if (!(obj instanceof GrammarFile)) {
            return false;
        }

        return memoryBuffer.equals(((GrammarFile) obj).memoryBuffer);
    }
}
