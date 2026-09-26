package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.PercentDecoder;
import org.apache.coyote.http11.exception.BadRequestException;

import java.util.Locale;
import java.util.Objects;

public class RequestPath {
    private static final String ENCODED_SLASH = "%2F";
    private static final String ROOT = "/";
    private static final String SEGMENT_DELIMITER = "/";
    private static final String PARENT_SEGMENT = "..";
    private static final char BACKSLASH = '\\';
    private static final char NULL_CHAR = '\0';
    private static final int NOT_FOUND = -1;

    private final String value;

    private RequestPath(final String value) {
        this.value = value;
    }

    public static RequestPath from(final String rawPath) {
        if (rawPath == null || rawPath.isEmpty()) {
            throw new BadRequestException("경로가 비어 있습니다");
        }
        rejectEncodedSlash(rawPath);
        final String decoded = PercentDecoder.decodePath(rawPath);
        validate(decoded);
        return new RequestPath(decoded);
    }

    private static void rejectEncodedSlash(final String rawPath) {
        if (rawPath.toUpperCase(Locale.ROOT).contains(ENCODED_SLASH)) {
            throw new BadRequestException("인코딩된 슬래시는 허용되지 않습니다");
        }
    }

    private static void validate(final String path) {
        if (!path.startsWith(ROOT)) {
            throw new BadRequestException("경로는 /로 시작해야 합니다");
        }
        if (containsControlChar(path)) {
            throw new BadRequestException("경로에 제어 문자가 포함되어 있습니다");
        }
        if (path.indexOf(NULL_CHAR) != NOT_FOUND) {
            throw new BadRequestException("경로에 null 문자가 포함되어 있습니다");
        }
        if (path.indexOf(BACKSLASH) != NOT_FOUND) {
            throw new BadRequestException("경로에 역슬래시가 포함되어 있습니다");
        }
        if (containsParentSegment(path)) {
            throw new BadRequestException("상위 경로 참조는 허용되지 않습니다");
        }
    }

    private static boolean containsControlChar(final String path) {
        for (final char c : path.toCharArray()) {
            if (Character.isISOControl(c)) {   // 0x00~0x1F, 0x7F~0x9F
                return true;
            }
        }
        return false;
    }

    private static boolean containsParentSegment(final String path) {
        for (final String segment : path.split(SEGMENT_DELIMITER)) {
            if (PARENT_SEGMENT.equals(segment)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestPath other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
