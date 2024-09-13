package org.apache.coyote.http11.message.file;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResourceConvertor {

    private static final String STATIC_PATH = "static";

    private StaticResourceConvertor() {
    }

    public static Path convertToPath(String resourceName) {
        URL resource = StaticResourceConvertor.class.getClassLoader()
                .getResource(STATIC_PATH + resourceName);

        if (resource == null) {
            throw new IllegalArgumentException("해당 파일이 존재하지 않습니다.");
        }

        return Paths.get(resource.getPath());
    }

    public static String probeContentType(Path path) throws IOException {
        String mimeType = Files.probeContentType(path);
        if (mimeType.startsWith("text")) {
            return mimeType + ";charset=utf-8";
        }

        return mimeType;
    }
}
