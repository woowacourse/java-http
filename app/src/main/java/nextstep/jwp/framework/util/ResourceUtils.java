package nextstep.jwp.framework.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

    private static final List<String> PREFIXES = Arrays.asList("", "static/");

    private ResourceUtils() {}

    public static Path findPathOf(String resourceName) {
        return Paths.get(findUrlOf(resourceName).getPath());
    }

    public static URL findUrlOf(String resourceName) {
        return findByResourceName(resourceName).orElseThrow(() -> new IllegalArgumentException(resourceName + " 파일을 찾을 수 없습니다."));
    }

    private static Optional<URL> findByResourceName(String resourceName) {
        final String name = cutPrefix(resourceName);

        ClassLoader classLoader = Objects.requireNonNullElseGet(
                Thread.currentThread().getContextClassLoader(),
                ResourceUtils.class::getClassLoader);

        return PREFIXES.stream()
                       .map(prefix -> classLoader.getResource(prefix + name))
                       .filter(Objects::nonNull)
                       .findAny();
    }

    private static String cutPrefix(String resourceName) {
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }
        return resourceName;
    }

    public static boolean exists(String resourceName) {
        return findByResourceName(resourceName).isPresent();
    }

    public static String readString(String resourceName) {
        final String resource;
        try {
            resource = Files.readString(findPathOf(resourceName));
        } catch (IOException e) {
            LOGGER.error("파일 읽기 작업 중 오류가 발생했습니다.", e);

            throw new IllegalStateException();
        }
        return resource;
    }

    public static String getFileExtension(String resourceName) {
        return com.google.common.io.Files.getFileExtension(resourceName);
    }
}
