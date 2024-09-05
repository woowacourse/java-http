package org.apache.coyote.http11.response;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ViewResolver {

    private static final String DEFAULT_RELATIVE_DIRECTORY_PATH = "tomcat/src/main/resources/static";
    private static final String DEFAULT_DIRECTORY_NAME = "static";
    private static final File DEFAULT_DIRECTORY_FILE = new File(DEFAULT_RELATIVE_DIRECTORY_PATH);

    public Optional<Path> findViewPath(String url) {
        try {
            URL foundUrl = ClassLoader.getSystemClassLoader().getResource(DEFAULT_DIRECTORY_NAME + url);

            if (foundUrl == null) {
                return findDefaultDirectory(url.substring(1));
            }
            return Optional.of(Path.of(foundUrl.toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("잘못된 view 파일 경로입니다.");
        }
    }

    private Optional<Path> findDefaultDirectory(String url) {
        String foundUrl = Stream.of(Objects.requireNonNull(DEFAULT_DIRECTORY_FILE.list()))
                .filter(fileName -> fileName.startsWith(url))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(url + "파일을 찾을 수 없습니다."));
        return findViewPath("/" + foundUrl);
    }
}
