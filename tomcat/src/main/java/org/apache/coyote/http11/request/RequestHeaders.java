package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpToken;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.ContentTooLargeException;
import org.apache.coyote.http11.exception.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RequestHeaders {
    private static final Set<String> SINGLE_VALUE_HEADERS = Set.of(
            HttpHeaderName.CONTENT_LENGTH.getNormalized(),
            HttpHeaderName.HOST.getNormalized(),
            HttpHeaderName.CONTENT_TYPE.getNormalized()
    );

    // Host = uri-host [ ":" port ], 허용 문자만 검사 (RFC 3986 reg-name, IP 리터럴, 포트)
    private static final String HOST_SPECIAL_CHARS = "-._~%!$&'()*+,;=:[]";
    private static final char FIRST_VISIBLE = 0x21;
    private static final char LAST_VISIBLE = 0x7E;
    private static final char FIRST_OBS_TEXT = 0x80;
    private static final int MAX_CONTENT_LENGTH = 2 * 1024 * 1024;

    private static final String LIST_SEPARATOR = ", ";
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String HEADER_DELIMITER = ":";
    private static final char SP = ' ';
    private static final char HTAB = '\t';

    private static final int NOT_FOUND = -1;
    private static final int NO_CONTENT = 0;

    private final Map<String, String> headers;
    private final int contentLength;

    private RequestHeaders(final Map<String, String> headers, final int contentLength) {
        this.headers = headers;
        this.contentLength = contentLength;
    }

    public static RequestHeaders from(final List<String> lines) {

        final Map<String, String> parsed = new HashMap<>();

        for (final String line : lines) {
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

            final String name = HttpHeaderName.normalize(rawName);
            final String value = parseValue(line.substring(delimiterIndex + 1));

            if (SINGLE_VALUE_HEADERS.contains(name) && parsed.containsKey(name)) {
                throw new BadRequestException("중복될 수 없는 헤더입니다: " + name);
            }
            parsed.merge(name, value, (existing, added) -> combine(name, existing, added));
        }

        final Map<String, String> headers = Map.copyOf(parsed);
        validateMessageFraming(headers);
        validateHost(headers);
        return new RequestHeaders(headers, parseContentLength(headers));
    }

    // RFC 9110 5.3: 같은 이름의 헤더는 순서대로 합친다. Cookie는 RFC 9113 8.2.3에 따라 "; "로 합친다.
    private static String combine(final String name, final String existing, final String added) {
        if (existing.isEmpty()) {
            return added;
        }
        if (added.isEmpty()) {
            return existing;
        }
        final String separator = HttpHeaderName.COOKIE.getNormalized().equals(name)
                ? COOKIE_SEPARATOR
                : LIST_SEPARATOR;
        return existing + separator + added;
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

    private static void validateMessageFraming(final Map<String, String> headers) {
        final boolean hasTransferEncoding = headers.containsKey(HttpHeaderName.TRANSFER_ENCODING.getNormalized());
        if (!hasTransferEncoding) {
            return;
        }
        final boolean hasContentLength = headers.containsKey(HttpHeaderName.CONTENT_LENGTH.getNormalized());
        if (hasContentLength) {
            throw new BadRequestException("Transfer-Encoding과 Content-Length를 함께 사용할 수 없습니다");
        }
        throw new NotImplementedException("Transfer-Encoding은 지원하지 않습니다");
    }

    private static void validateHost(final Map<String, String> headers) {
        final String host = headers.get(HttpHeaderName.HOST.getNormalized());
        if (host == null) {
            return;   // 없는 경우는 버전을 아는 HttpRequest가 판단
        }
        if (host.isEmpty()) {
            throw new BadRequestException("Host가 비어 있습니다");
        }
        for (final char c : host.toCharArray()) {
            if (!isHostChar(c)) {
                throw new BadRequestException("Host 형식이 잘못되었습니다");
            }
        }
    }

    private static boolean isHostChar(final char c) {
        return ('A' <= c && c <= 'Z')
                || ('a' <= c && c <= 'z')
                || ('0' <= c && c <= '9')
                || HOST_SPECIAL_CHARS.indexOf(c) != -1;
    }

    public boolean hasHost() {
        return headers.containsKey(HttpHeaderName.HOST.getNormalized());
    }

    private static int parseContentLength(final Map<String, String> headers) {
        final String value = headers.get(HttpHeaderName.CONTENT_LENGTH.getNormalized());
        if (value == null) {
            return NO_CONTENT;
        }
        if (value.isEmpty()) {
            throw new BadRequestException("Content-Length가 비어 있습니다");
        }
        long length = 0;
        for (final char c : value.toCharArray()) {
            if (c < '0' || c > '9') {
                throw new BadRequestException("Content-Length 형식이 잘못되었습니다");
            }
            length = length * 10 + (c - '0');
            if (length > MAX_CONTENT_LENGTH) {
                throw new ContentTooLargeException("요청 본문이 너무 큽니다");
            }
        }
        return (int) length;
    }

    public Optional<String> get(final HttpHeaderName name) {
        return Optional.ofNullable(headers.get(name.getNormalized()));
    }

    public int getContentLength() {
        return contentLength;
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(get(HttpHeaderName.COOKIE).orElse(""));
    }
}
