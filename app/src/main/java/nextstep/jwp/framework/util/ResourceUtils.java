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

public class ResourceUtils {

    private static final List<String> PREFIXES = Arrays.asList("", "static");

    private ResourceUtils() {}

    public static Path findPathOf(String resourceName) {
        return Paths.get(findUrlOf(resourceName).getPath());
    }

    public static URL findUrlOf(String resourceName) {
        return findByResourceName(resourceName).orElseThrow(() -> new IllegalArgumentException(resourceName + " 파일을 찾을 수 없습니다."));
    }

    private static Optional<URL> findByResourceName(String resourceName) {
        ClassLoader classLoader = Objects.requireNonNullElseGet(
                Thread.currentThread().getContextClassLoader(),
                ResourceUtils.class::getClassLoader);

        return PREFIXES.stream()
                       .map(prefix -> classLoader.getResource(prefix + resourceName))
                       .filter(Objects::nonNull)
                       .findAny();
    }

    public static boolean exists(String resourceName) {
        return findByResourceName(resourceName).isPresent();
    }

    public static String readString(String resourceName) {
        final String resource;
        try {
            resource = Files.readString(findPathOf(resourceName));
        } catch (IOException e) {
            e.printStackTrace();

            throw new IllegalStateException("파일 읽기 작업 중 오류가 발생했습니다.");
        }
        return resource;
    }

    public static String getFileExtension(String resourceName) {
        return com.google.common.io.Files.getFileExtension(resourceName);
    }
}
