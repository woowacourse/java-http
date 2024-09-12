package org.apache.coyote.http11.httpmessage.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class StaticResource {
    private final File file;

    public StaticResource(String resourcePath) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            throw new NoSuchFileException(resourcePath + " 경로의 파일이 존재하지 않습니다.");
        }
        this.file = new File(resource.getPath());
    }

    public String getContentType() throws IOException {
        return Files.probeContentType(file.toPath());
    }

    public String getContent() throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public long getContentLength() throws IOException {
        return getContent().getBytes().length;
    }
}
