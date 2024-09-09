package org.apache.catalina.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewResolver {

    public String resolve(String resourcePath) {
        try {
            final URL resource = getClass().getClassLoader().getResource(resourcePath);
            if (resource != null) {
                final Path path = Paths.get(resource.toURI());
                return Files.readString(path);
            }
            return null;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("응답 파일이 존재하지 않습니다.");
        }
    }
}
