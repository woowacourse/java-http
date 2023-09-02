package org.apache.coyote.common;

import org.apache.coyote.exception.CoyoteHttpException;
import org.apache.coyote.exception.CoyoteIOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private static final int REQUEST_FIRST_LINE_LENGTH = 3;
    private static final String REQUEST_DELIMITER = " ";
    private static final String HEADER_END_CONDITION = "";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final URI requestUri;
    private final HttpVersion httpVersion;
    private final Headers headers;

    public HttpRequest(final HttpMethod httpMethod, final URI requestUri, final HttpVersion httpVersion, final Headers headers) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public static HttpRequest from(final BufferedReader br) {
        try (br) {
            final String[] requestInformation = br.readLine().split(REQUEST_DELIMITER);
            if (requestInformation.length != REQUEST_FIRST_LINE_LENGTH) {
                throw new CoyoteHttpException("HTTP 요청으로 들어온 값의 첫 번째 라인에 HttpMethod, URI, HttpVersion가 존재해야 합니다.");
            }

            final List<String> headersWithValue = new ArrayList<>();
            String header = br.readLine();
            while (!header.equals(HEADER_END_CONDITION)) {
                headersWithValue.add(header);
                header = br.readLine();
            }

            final Headers headers = new Headers(headersWithValue);
            final HttpMethod httpMethod = HttpMethod.from(requestInformation[HTTP_METHOD_INDEX]);
            final URI requestUri = URI.create(requestInformation[REQUEST_URI_INDEX]);
            final HttpVersion httpVersion = HttpVersion.from(requestInformation[HTTP_VERSION_INDEX]);

            return new HttpRequest(httpMethod, requestUri, httpVersion, headers);
        } catch (IOException e) {
            throw new CoyoteIOException("HTTP 요청 정보를 읽던 도중에 예외가 발생하였습니다.");
        }
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public URI requestUri() {
        return requestUri;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public Headers headers() {
        return headers;
    }
}
