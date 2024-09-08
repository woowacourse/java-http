package org.apache.coyote.file;

public class Resource {
    private final String fileName;
    private final byte[] bytes;

    public Resource(final String fileName, final byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public FileExtension getExtension() {
        final int index = fileName.lastIndexOf('.');
        return FileExtension.from(index == -1 ? "" : fileName.substring(index + 1));
    }

    public long length() {
        return bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
