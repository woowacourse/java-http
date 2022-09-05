package org.apache.coyote.http11.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.enums.ContentType;

public class FileUtil {

    private static final String DEFAULT_PATH = "static";

    public static File findFile(final String path) {
        final String resource = DEFAULT_PATH + path;

        final URL url = FileUtil.class
                .getClassLoader()
                .getResource(resource);

        if (Objects.isNull(url)) {
            throw new IllegalArgumentException(String.format("유효하지 않은 경로입니다. -> resource: %s", resource));
        }

        return new File(url.getFile());
    }

    public static String findContentType(final File file) {
        final String[] fileNameInfo = file.getName().split("\\.");
        final ContentType contentType = ContentType.of(fileNameInfo[fileNameInfo.length - 1]);
        return contentType.getValue();
    }

    public static String generateFile(File file) {
        try {
            final Path path = file.toPath();
            return new String(Files.readAllBytes(path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
