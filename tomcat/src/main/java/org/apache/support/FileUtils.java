package org.apache.support;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.coyote.http11.exception.NotFoundException;

public class FileUtils {

    private static final String STATIC_DIRECTORY = "static";

    public static String getMimeTypeFromPath(final String filePath) {
        final Path path = Paths.get(filePath);
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(String.format("파일 읽기 실패 %s", filePath));
        }
    }

    public static String getStaticFilePathFromUri(final String uri) {
        return getResourceFromUri(uri)
                .orElseThrow(() -> new NotFoundException(String.format("찾는 파일이 존재하지 않습니다. %s", uri)))
                .getPath();
    }

    public static Optional<URL> getResourceFromUri(final String uri) {
        final URL resource = FileUtils.class
                .getClassLoader()
                .getResource(STATIC_DIRECTORY + uri);

        return Optional.ofNullable(resource);
    }

    public static String readContentByStaticFilePath(final String filePath) {
        try {
            return new String(Files.readAllBytes(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(String.format("파일을 읽을 수 없습니다. %s", filePath), e);
        }
    }
}
