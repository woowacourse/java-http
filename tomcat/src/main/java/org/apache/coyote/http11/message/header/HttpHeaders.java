package org.apache.coyote.http11.message.header;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.message.CookieParser;

public class HttpHeaders {

    private static final String HTTP_HEADER_FIELD_SEPARATOR = "\r\n";
    private static final String HTTP_HEADER_FIELD_KEY_AND_VALUE_SEPARATOR = ": ";

    private final List<HttpHeaderField> values;

    public HttpHeaders(final String httpHeaders) {
        validateHttpHeadersIsNullOrBlank(httpHeaders);
        this.values = parseHttpHeaderFields(httpHeaders);
    }

    public HttpHeaders(final List<HttpHeaderField> values) {
        this.values = values;
    }

    private void validateHttpHeadersIsNullOrBlank(final String httpHeaders) {
        if (StringUtils.isBlank(httpHeaders)) {
            throw new IllegalArgumentException("HTTP Headers는 null 혹은 빈 값이 입력될 수 없습니다. - " + httpHeaders);
        }
    }

    private List<HttpHeaderField> parseHttpHeaderFields(final String httpHeaders) {
        return Arrays.stream(httpHeaders.split(HTTP_HEADER_FIELD_SEPARATOR))
                .map(HttpHeaderField::new)
                .toList();
    }

    public Map<String, String> parseCookie(final CookieParser parser) {
        final Optional<HttpHeaderField> cookieFile = values.stream()
                .filter(value -> value.matchKey(HttpHeaderFieldType.COOKIE.getValue()))
                .findAny();

        return cookieFile.map(field -> parser.parseCookie(field.getValue()))
                .orElse(Collections.emptyMap());
    }

    public String convertHeaderMessage() {
        StringBuilder sb = new StringBuilder();
        final List<String> httpFieldMessages = values.stream()
                .map(HttpHeaderField::convertHeaderFieldMessage)
                .toList();
        httpFieldMessages.forEach(message -> sb.append(message).append(" ").append(HTTP_HEADER_FIELD_SEPARATOR));

        return sb.toString();
    }
}
