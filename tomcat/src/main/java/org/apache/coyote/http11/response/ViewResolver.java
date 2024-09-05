package org.apache.coyote.http11.response;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class ViewResolver {

    private static final String DEFAULT_DIRECTORY_NAME = "static";

    public Optional<Path> findViewPath(String url) {
        try {
            URL foundUrl = ClassLoader.getSystemClassLoader().getResource(DEFAULT_DIRECTORY_NAME + url);

            if (foundUrl == null) {
                return Optional.empty();
            }
            return Optional.of(Path.of(foundUrl.toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("잘못된 view 파일 경로입니다.");
        }
    }
}
