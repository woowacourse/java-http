package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseFile {

    private static final String UTF_8_CHARSET = ";charset=utf-8";

    private final String contentType;
    private final byte[] content;

    public ResponseFile(String contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseFile of(URL resourceUrl) {
        File file = new File(resourceUrl.getFile());
        Path path = file.toPath();
        try {
            String contentType = Files.probeContentType(path) + UTF_8_CHARSET;
            byte[] content = Files.readAllBytes(path);
            return new ResponseFile(contentType, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContent() {
        return new String(content);
    }

    public String getContentType() {
        return contentType;
    }

    public int size() {
        return content.length;
    }
}
