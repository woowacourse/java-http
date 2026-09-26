package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpToken;
import org.apache.coyote.http11.exception.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestHeaders {
    private static final String HEADER_DELIMITER = ":";
    private static final int NOT_FOUND = -1;
    private static final char SP = ' ';
    private static final char HTAB = '\t';
    private static final char FIRST_VISIBLE = 0x21;
    private static final char LAST_VISIBLE = 0x7E;
    private static final char FIRST_OBS_TEXT = 0x80;

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> headers) {

        final Map<String, String> parsed = new HashMap<>();

        for (final String line : headers) {
            if (isObsFold(line)) {
                throw new BadRequestException("obs-fold(헤더 줄 접기)는 지원하지 않습니다");
            }
            final int delimiterIndex = line.indexOf(HEADER_DELIMITER);
            if (delimiterIndex == NOT_FOUND) {
                throw new BadRequestException("잘못된 형식의 헤더: " + line);
            }
            final String rawName = line.substring(0, delimiterIndex);
            if (!HttpToken.isValid(rawName)) {
                throw new BadRequestException("잘못된 헤더 이름입니다");
            }

            final String name = HttpHeaderName.normalize(line.substring(0, delimiterIndex));
            final String value = parseValue(line.substring(delimiterIndex + 1));

            if (HttpHeaderName.CONTENT_LENGTH.getNormalized().equals(name) && parsed.containsKey(name)) {
                throw new BadRequestException("Content-Length 헤더가 중복되었습니다");
            }
            parsed.putIfAbsent(name, value);
        }
        return new RequestHeaders(Map.copyOf(parsed));
    }

    private static String parseValue(final String raw) {
        final String value = trimOws(raw);
        for (final char c : value.toCharArray()) {
            if (!isFieldValueChar(c)) {
                throw new BadRequestException("헤더 값에 허용되지 않는 문자가 포함되어 있습니다");
            }
        }
        return value;
    }

    private static String trimOws(final String value) {
        int start = 0;
        int end = value.length();
        while (start < end && isOws(value.charAt(start))) {
            start++;
        }
        while (end > start && isOws(value.charAt(end - 1))) {
            end--;
        }
        return value.substring(start, end);
    }

    private static boolean isOws(final char c) {
        return c == SP || c == HTAB;
    }

    private static boolean isFieldValueChar(final char c) {
        return isOws(c)
                || (FIRST_VISIBLE <= c && c <= LAST_VISIBLE)
                || c >= FIRST_OBS_TEXT;
    }

    private static boolean isObsFold(final String line) {
        if (line.isEmpty()) {
            return false;
        }
        final char first = line.charAt(0);
        return first == SP || first == HTAB;
    }

    public Optional<String> get(final HttpHeaderName name) {
        return Optional.ofNullable(headers.get(name.getNormalized()));
    }

    public int getContentLength() {
        final Optional<String> value = get(HttpHeaderName.CONTENT_LENGTH);
        if (value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.get());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Content-Length가 숫자가 아닙니다.");
        }
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(get(HttpHeaderName.COOKIE).orElse(""));
    }
}
