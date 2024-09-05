package org.apache.coyote.http11;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class ViewResolver {

    private static final String DEFAULT_DIRECTORY_NAME = "static";

    public Path findViewPath(String url) {
        URL foundUrl = getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_NAME + url);
        try {
            return Path.of(Objects.requireNonNull(foundUrl).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("잘못된 view 파일 경로입니다.");
        }
    }
}
