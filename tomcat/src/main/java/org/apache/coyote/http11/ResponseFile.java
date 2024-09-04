package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseFile {

    private final String contentType;
    private final String content;

    public ResponseFile(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseFile of(URL resource) throws IOException {
        File resourceFile = new File(resource.getFile());
        Path resourceFilePath = resourceFile.toPath();
        String contentType = Files.probeContentType(resourceFilePath) + ";charset=utf-8";
        String responseBody = new String(Files.readAllBytes(resourceFilePath));

        return new ResponseFile(contentType, responseBody);
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
