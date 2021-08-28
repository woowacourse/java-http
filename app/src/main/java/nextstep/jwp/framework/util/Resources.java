package nextstep.jwp.framework.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Resources {
    private Resources() {}

    public static Path findPathOf(String resourceName) {
        return Paths.get(findUrlOf(resourceName).getPath());
    }

    public static URL findUrlOf(String resourceName) {
        ClassLoader classLoader = Objects.requireNonNullElseGet(
                Thread.currentThread().getContextClassLoader(),
                Resources.class::getClassLoader);

        URL url = classLoader.getResource(resourceName);
        return Objects.requireNonNull(url, resourceName + " 파일을 찾을 수 없습니다.");
    }

    public static String readString(String resourceName) throws IOException {
        return Files.readString(findPathOf(resourceName));
    }
}
