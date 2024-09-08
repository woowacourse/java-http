package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class Content {
    private final String contentType;
    private final String content;

    public Content(String resourceName) throws IOException {
        this.contentType = determineContentType(resourceName);
        String path = "static" + resourceName;
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new NoSuchFileException("파일 " + resourceName + "이 존재하지 않습니다.");
        }
        this.content = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
    }

    private String determineContentType(String resourceName) {
        String extension = resourceName.split("\\.")[1];
        if (extension.equals("html")) {
            return "text/html";
        }
        if (extension.equals("css")) {
            return "text/css";
        }
        return "text/plain";
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
