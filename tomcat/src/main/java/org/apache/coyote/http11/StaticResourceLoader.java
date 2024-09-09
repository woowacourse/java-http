package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceLoader {
    public String load(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new RuntimeException("파일이 존재하지 않습니다 : %s".formatted(path));
        }
        try {
            File file = new File(resource.toURI());
            return Files.readString(file.toPath());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
