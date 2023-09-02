package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.InputStream;

public class StaticResource {

    private final byte[] bytes;
    private final String fileExtension;

    private StaticResource(byte[] bytes, String fileExtension) {
        this.bytes = bytes;
        this.fileExtension = fileExtension;
    }

    public static StaticResource of(String uri) throws IOException {
        try {
            InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("static" + uri);
            byte[] bytes = resourceAsStream.readAllBytes();
            return new StaticResource(bytes, extractFileExtension(uri));
        } catch (NullPointerException e) {
            throw new IOException(e);
        }
    }

    private static String extractFileExtension(String uri) {
        return uri.split("\\.")[1].toLowerCase();
    }

    public String fileToString() {
        return new String(bytes);
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
