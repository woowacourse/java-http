package org.apache.coyote.httpresponse.header;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContentLengthHeader implements ResponseHeader {

    private static final String DELIMITER = ": ";

    private final Path path;

    public ContentLengthHeader(final Path path) {
        this.path = path;
    }

    @Override
    public String getKeyAndValue(final ResponseHeaderType headerType) {
        return headerType.getHeaderName() + DELIMITER + countChar();
    }

    private long countChar() {
        try {
            long charCount = 0;
            for (final String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                charCount += line.length();
            }
            return charCount;
        } catch (IOException e) {
            throw new RuntimeException("응답 시에 파일을 읽을 수 없습니다.");
        }
    }
}
