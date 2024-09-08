package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Content {
    private final String contentType;
    private final String content;

    public Content(String resourceName) throws IOException {
        String path = "static" + resourceName;
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new NoSuchFileException("파일 " + resourceName + "이 존재하지 않습니다.");
        }
        Path resourcePath = new File(resource.getPath()).toPath();
        this.content = new String(Files.readAllBytes(resourcePath));
        this.contentType = Files.probeContentType(resourcePath);
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
