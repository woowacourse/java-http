package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class StaticResource {
    private final File file;

    public StaticResource(String resourceName) throws IOException {
        String path = "static" + resourceName;
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new NoSuchFileException("파일 " + resourceName + "이 존재하지 않습니다.");
        }
        this.file = new File(resource.getPath());
    }

    public String getContentType() throws IOException {
        return Files.probeContentType(file.toPath());
    }

    public String getContent() throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
