package nextstep.jwp.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import nextstep.jwp.exception.ResourceNotFoundException;

public class ResourcesUtil {

    private static final String STATIC_RESOURCES_PATH = "/static/%s";
    private static final String STATIC_EXTENTION_DOT = ".";
    private static final String DEFAULT_STATIC_EXTENSION = ".html";

    private ResourcesUtil() {
    }

    public static String readResource(final String path, final Class<?> classes) {
        try {
            URL url = calculateStaticUri(path, classes);
            Objects.requireNonNull(url);
            Path filePath = Paths.get(url.toURI());
            return Files.readString(filePath);
        } catch (URISyntaxException | IOException | NullPointerException e) {
            throw new ResourceNotFoundException("파일을 찾을 수 없습니다.");
        }
    }

    private static URL calculateStaticUri(final String uri, final Class<?> classes) {
        if (!isStaticUri(uri)) {
            return getURL(String.format(STATIC_RESOURCES_PATH, uri + DEFAULT_STATIC_EXTENSION), classes);
        }
        return getURL(String.format(STATIC_RESOURCES_PATH, uri), classes);
    }

    private static boolean isStaticUri(final String uri) {
        return uri.contains(STATIC_EXTENTION_DOT);
    }

    private static URL getURL(final String path, final Class<?> classes) {
        return classes.getResource(path);
    }
}
