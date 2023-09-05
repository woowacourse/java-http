package org.apache.coyote.request;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.exception.CoyoteIOException;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;

public class HttpRequest {

    private static final String HEADER_END_CONDITION = "";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final MediaType mediaType;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine,
                        final RequestHeaders requestHeaders,
                        final MediaType mediaType,
                        final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.mediaType = mediaType;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader br) {
        try {
            final RequestLine requestLine = RequestLine.from(br.readLine());
            final RequestHeaders requestHeaders = parseToHttpHeaders(br);
            final MediaType mediaType = MediaType.from(requestLine.requestPath().value());
            final RequestBody requestBody = parseToResponseBody(br, requestHeaders);

            return new HttpRequest(requestLine, requestHeaders, mediaType, requestBody);
        } catch (IOException e) {
            throw new CoyoteIOException("HTTP 요청 정보를 읽던 도중에 예외가 발생하였습니다.");
        }
    }

    private static RequestHeaders parseToHttpHeaders(final BufferedReader br) throws IOException {
        final List<String> headersWithValue = new ArrayList<>();
        String header = br.readLine();
        while (!header.equals(HEADER_END_CONDITION)) {
            headersWithValue.add(header);
            header = br.readLine();
        }

        return RequestHeaders.from(headersWithValue);
    }

    private static RequestBody parseToResponseBody(final BufferedReader br, final RequestHeaders requestHeaders) throws IOException {
        final String contentLengthHeader = requestHeaders.getHeaderValue(CONTENT_LENGTH.value());
        if (Objects.isNull(contentLengthHeader)) {
            return RequestBody.empty();
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public String getCookieValue(final String cookieName) {
        return requestHeaders.getCookieValue(cookieName);
    }

    public Headers headers() {
        return requestHeaders.headers();
    }

    public Cookies cookies() {
        return requestHeaders.cookies();
    }

    public Session session() {
        return requestHeaders.session();
    }

    public MediaType mediaType() {
        return mediaType;
    }

    public RequestBody requestBody() {
        return requestBody;
    }

    @Override
    public String toString() {
        return "HttpRequest{" + System.lineSeparator() +
               "    requestLine = " + requestLine + ", " + System.lineSeparator() +
               "    requestHeaders = " + requestHeaders + System.lineSeparator() +
               "    mediaType = " + mediaType + System.lineSeparator() +
               "    requestBody = " + requestBody + System.lineSeparator() +
               '}';
    }
}
