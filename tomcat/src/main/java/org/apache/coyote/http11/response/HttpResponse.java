package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaderName;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class HttpResponse {
    private static final String CRLF = "\r\n";
    private static final String SP = " ";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String HEADER_DELIMITER = ": ";
    // 상태 줄과 헤더 줄 끝의 공백. 스펙상 허용된다 (reason-phrase의 SP, 헤더 값 뒤의 OWS)
    private static final String LINE_SUFFIX = " ";

    private static final char HTAB = '\t';
    private static final char FIRST_VISIBLE = 0x21;
    private static final char LAST_VISIBLE = 0x7E;
    private static final byte[] EMPTY_BODY = new byte[0];

    private static final Set<HttpHeaderName> FRAMING_HEADERS = Set.of(
            HttpHeaderName.CONTENT_LENGTH,
            HttpHeaderName.TRANSFER_ENCODING
    );

    private final Map<HttpHeaderName, List<String>> headers = new LinkedHashMap<>();
    private HttpStatus status = HttpStatus.OK;
    private byte[] body = EMPTY_BODY;

    /** 에러 응답을 새로 만든다. 처리 중 채워진 헤더(Location, Set-Cookie 등)가 섞이지 않는다 */
    public static HttpResponse error(final HttpStatus status) {
        final HttpResponse response = new HttpResponse();
        response.sendError(status);
        return response;
    }

    public void setStatus(final HttpStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    /** 같은 이름의 헤더를 이 값 하나로 교체한다 */
    public void setHeader(final HttpHeaderName name, final String value) {
        validateSettable(name);
        validateHeaderValue(value);
        final List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values);
    }

    /** 같은 이름의 헤더를 별도의 줄로 추가한다 */
    public void addHeader(final HttpHeaderName name, final String value) {
        validateSettable(name);
        validateHeaderValue(value);
        headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }

    public void setContentType(final ContentType contentType) {
        setHeader(HttpHeaderName.CONTENT_TYPE, contentType.getValue());
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        setHeader(HttpHeaderName.LOCATION, location);
        setBody(EMPTY_BODY);
    }

    public void sendError(final HttpStatus status) {
        setStatus(status);
        setContentType(ContentType.HTML);
        setBody((status.getCode() + SP + status.getReasonPhrase()).getBytes(StandardCharsets.US_ASCII));
    }

    /** Set-Cookie는 쉼표로 합칠 수 없어 쿠키마다 별도의 줄로 보낸다 (RFC 9110 5.3) */
    public void addCookie(final String cookie) {
        addHeader(HttpHeaderName.SET_COOKIE, cookie);
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final StringBuilder head = new StringBuilder();
        head.append(HTTP_VERSION).append(SP)
                .append(status.getCode()).append(SP)
                .append(status.getReasonPhrase())
                .append(LINE_SUFFIX).append(CRLF);

        headers.forEach((name, values) ->
                values.forEach(value -> appendHeader(head, name, value)));

        appendHeader(head, HttpHeaderName.CONTENT_LENGTH, String.valueOf(body.length));
        head.append(CRLF);

        outputStream.write(head.toString().getBytes(StandardCharsets.US_ASCII));
        outputStream.write(body);
        outputStream.flush();
    }

    private static void appendHeader(final StringBuilder head, final HttpHeaderName name, final String value) {
        head.append(name.getValue())
                .append(HEADER_DELIMITER)
                .append(value)
                .append(LINE_SUFFIX)
                .append(CRLF);
    }

    // 응답 분할(response splitting) 방지: CR, LF 등 제어 문자와 비ASCII 문자를 거부한다
    private static void validateHeaderValue(final String value) {
        Objects.requireNonNull(value);
        for (final char c : value.toCharArray()) {
            final boolean allowed = c == ' ' || c == HTAB || (FIRST_VISIBLE <= c && c <= LAST_VISIBLE);
            if (!allowed) {
                throw new IllegalArgumentException("응답 헤더 값에 허용되지 않는 문자가 포함되어 있습니다");
            }
        }
    }

    private static void validateSettable(final HttpHeaderName name) {
        if (FRAMING_HEADERS.contains(name)) {
            throw new IllegalArgumentException(name.getValue() + " 헤더는 본문에서 계산되므로 직접 설정할 수 없습니다");
        }
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Optional<String> getHeader(final HttpHeaderName name) {
        return getHeaders(name).stream().findFirst();
    }

    public List<String> getHeaders(final HttpHeaderName name) {
        return List.copyOf(headers.getOrDefault(name, List.of()));
    }

    public byte[] getBody() {
        return body.clone();
    }
}
