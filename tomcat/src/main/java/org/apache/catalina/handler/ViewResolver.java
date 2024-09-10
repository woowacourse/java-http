package org.apache.catalina.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewResolver {

    public String resolve(String resourcePath) {
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("해당 경로의 응답 파일이 존재하지 않습니다: " + resourcePath);
        }
        try {
            final Path path = Paths.get(resource.toURI());
            return Files.readString(path);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("해당 경로의 응답 파일을 읽을 수 없습니다: " + resourcePath);
        }
    }
}
