package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ResponseFile {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String CONTENT_TYPE_CHARSET_DELIMITER = ";";
    private static final String UTF_8_CHARSET = "charset=utf-8";
    private static final Set<String> TEXT_BASED_KEYWORD = Set.of("text", "javascript", "xml", "json");

    private final String contentType;
    private final byte[] content;

    public ResponseFile(String contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public static ResponseFile of(URL resourceUrl) {
        File file = new File(resourceUrl.getFile());
        Path path = file.toPath();
        String contentType = detectContentType(path);
        try {
            byte[] content = Files.readAllBytes(path);
            return new ResponseFile(contentType, content);
        } catch (IOException e) {
            throw new IllegalStateException("파일 읽기 실패: " + file, e);
        }
    }

    private static String detectContentType(Path path) {
        String contentType;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException ignored) {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        if (isCharsetRequired(contentType)) {
            return String.join(CONTENT_TYPE_CHARSET_DELIMITER, contentType, UTF_8_CHARSET);
        }
        return contentType;
    }

    private static boolean isCharsetRequired(String contentType) {
        return TEXT_BASED_KEYWORD.stream()
                .anyMatch(contentType::contains);
    }

    public String getContent() {
        return new String(content);
    }

    public String getContentType() {
        return contentType;
    }

    public int size() {
        return content.length;
    }
}
