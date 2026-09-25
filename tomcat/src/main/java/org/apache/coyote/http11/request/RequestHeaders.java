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

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> headers) {

        final Map<String, String> parsed = new HashMap<>();

        for (final String line : headers) {
            if (line.isBlank()) {
                continue;
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
            final String value = line.substring(delimiterIndex + 1).strip();

            if (HttpHeaderName.CONTENT_LENGTH.getNormalized().equals(name) && parsed.containsKey(name)) {
                throw new BadRequestException("Content-Length 헤더가 중복되었습니다");
            }
            parsed.putIfAbsent(name, value);
        }
        return new RequestHeaders(Map.copyOf(parsed));
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
