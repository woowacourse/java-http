package org.apache.catalina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.util.ResourceDecodedUrl;

public class StaticResourceUtils {

    private static final String FILE_EXTENSION_DELIMITER = ".";

    public static String readResourceContent(final String requestPath){
        if (Objects.equals(requestPath, "/")) {
            return "Hello world!";
        }
        String resourcePath = getResourcePath(requestPath);
        ResourceDecodedUrl resourceDecodedUrl = ResourceDecodedUrl.from(resourcePath);
        try {
            return new String(Files.readAllBytes(Path.of(resourceDecodedUrl.value())));
        } catch (IOException e) {
            throw new RuntimeException("파일 -> 텍스트 변환 과정 실패");
        }
    }

    public static String getContentType(final String requestPath) {
        String resourcePath = getResourcePath(requestPath);
        String extension = resourcePath.substring(resourcePath.lastIndexOf(FILE_EXTENSION_DELIMITER) + 1)
                .toLowerCase();

        return switch (extension) {
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "svg" -> "image/svg+xml";
            case "html" -> "text/html";
            default -> throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        };
    }

    private static String getResourcePath(final String requestPath) {
        String prefixPath = "static";
        if (requestPath.contains(FILE_EXTENSION_DELIMITER)) {
            return prefixPath + requestPath;
        }
        return prefixPath + requestPath + ".html";
    }
}
