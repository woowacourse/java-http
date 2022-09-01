package org.apache.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ResponseUtil {

    public static String getResponseBody(final String uri, final Class<?> ClassType) {
        try {
            if (uri.equals("/")) {
                return "Hello world!";
            }
            final URL url = Objects.requireNonNull(ClassType.getClassLoader().getResource("static" + uri));
            final Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException | NullPointerException e) {
            throw new IllegalArgumentException("파일 찾기 에러");
        }
    }
}
