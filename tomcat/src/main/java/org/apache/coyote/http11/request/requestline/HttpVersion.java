package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.exception.HttpVersionNotSupportedException;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private static final Pattern VERSION_FORMAT = Pattern.compile("HTTP/[0-9]\\.[0-9]");

    private static final Map<String, HttpVersion> BY_VALUE = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(HttpVersion::getValue, Function.identity()));

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    static HttpVersion from(final String token) {
        if (token == null) {
            throw new BadRequestException("HTTP 버전이 없습니다");
        }
        final HttpVersion version = BY_VALUE.get(token);
        if (version != null) {
            return version;
        }
        if (!VERSION_FORMAT.matcher(token).matches()) {
            throw new BadRequestException("잘못된 HTTP 버전 형식입니다");
        }
        throw new HttpVersionNotSupportedException("지원하지 않는 HTTP 버전입니다");
    }

    public String getValue() {
        return value;
    }
}
