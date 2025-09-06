package org.apache.coyote.util;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.http11.Http11Processor;
import org.apache.exception.StaticResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtil {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean isStaticResourceExist(final String path, final Class<?> classLoader) {
        final String STATIC_PATH = "/static";

        try {
            URL url = Objects.requireNonNull(
                    classLoader.getResource(STATIC_PATH + path)
            );
            // uri 정규화(예: /static/../static/index.html -> /static/index.html)
            URI uri = url.toURI().normalize();
            Path filePath = Paths.get(uri);

            // 정규화 후 경로가 static 디렉토리 안에 있는지 확인
            Path staticRoot = Paths.get(Objects.requireNonNull(
                    classLoader.getResource(STATIC_PATH)
            ).toURI());

            if (!filePath.startsWith(staticRoot)) {
                // 상위 경로 탈출 시도 차단
                log.debug("Insecure Path traversal attempt detected: {}", path);
                return false;
            }

            return Files.isRegularFile(filePath);
        } catch (Exception e) {
            log.debug("{}: file doesn't exist", path);
            return false;
        }
    }

    public static String readStaticResource(final String path, final Class<?> classLoader) {
        final String STATIC_PATH = "/static";

        try {
            URL url = Objects.requireNonNull(
                    classLoader.getResource(STATIC_PATH + path)
            );
            URI uri = url.toURI().normalize();
            Path filePath = Paths.get(uri);

            // 정규화 후 경로가 static 디렉토리 안에 있는지 확인
            Path staticRoot = Paths.get(Objects.requireNonNull(
                    classLoader.getResource(STATIC_PATH)
            ).toURI());

            if (!filePath.startsWith(staticRoot)) {
                // 상위 경로 탈출 시도 차단
                log.error("Insecure Path traversal attempt detected: {}", path);
                throw new SecurityException("Insecure Path traversal attempt detected: " + path);
            }

            return Files.readString(filePath);
        } catch (Exception e) {
            log.error("{}: file doesn't exist", path);
            throw new StaticResourceNotFoundException(path + ": file doesn't exist");
        }
    }
}
