package org.apache.coyote.request;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.HttpHeaders;
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
    private final HttpHeaders httpHeaders;
    private final MediaType mediaType;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine,
                        final HttpHeaders headersV2,
                        final MediaType mediaType,
                        final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.httpHeaders = headersV2;
        this.mediaType = mediaType;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader br) {
        try {
            final RequestLine requestLine = RequestLine.from(br.readLine());
            final HttpHeaders httpHeaders = parseToHttpHeaders(br);
            final MediaType mediaType = MediaType.from(requestLine.requestPath().source());
            final RequestBody requestBody = parseToResponseBody(br, httpHeaders);

            return new HttpRequest(requestLine, httpHeaders, mediaType, requestBody);
        } catch (IOException e) {
            throw new CoyoteIOException("HTTP 요청 정보를 읽던 도중에 예외가 발생하였습니다.");
        }
    }

    private static HttpHeaders parseToHttpHeaders(final BufferedReader br) throws IOException {
        final List<String> headersWithValue = new ArrayList<>();
        String header = br.readLine();
        while (!header.equals(HEADER_END_CONDITION)) {
            headersWithValue.add(header);
            header = br.readLine();
        }

        return HttpHeaders.from(headersWithValue);
    }

    private static RequestBody parseToResponseBody(final BufferedReader br, final HttpHeaders httpHeaders) throws IOException {
        final String contentLengthHeader = httpHeaders.getHeaderValue(CONTENT_LENGTH.source());
        if (Objects.isNull(contentLengthHeader)) {
            return RequestBody.EMPTY;
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public Headers headers() {
        return httpHeaders.headers();
    }

    public Cookies cookies() {
        return httpHeaders.cookies();
    }

    public Session session() {
        return httpHeaders.session();
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
               "    httpHeaders = " + httpHeaders + System.lineSeparator() +
               "    mediaType = " + mediaType + System.lineSeparator() +
               "    requestBody = " + requestBody + System.lineSeparator() +
               '}';
    }
}
