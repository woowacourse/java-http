package org.apache.catalina.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewResolver {

    private static final String STATIC_PATH_PREFIX = "static";

    public String resolve(String resourcePath) {
        final String staticPath = parseStaticPath(resourcePath);
        final URL resource = getClass().getClassLoader().getResource(staticPath);
        if (resource == null) {
            throw new IllegalArgumentException("해당 경로의 응답 파일이 존재하지 않습니다: " + staticPath); // TODO: 404 처리
        }
        try {
            final Path path = Paths.get(resource.toURI());
            return Files.readString(path);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("해당 경로의 응답 파일을 읽을 수 없습니다: " + staticPath);
        }
    }

    private String parseStaticPath(String filePath) {
        return STATIC_PATH_PREFIX + filePath;
    }
}
