package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpVersion;
import nextstep.jwp.model.HttpCookie;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final HttpCookie cookies;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final HttpCookie cookies, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.from(reader.readLine());
        final RequestHeaders requestHeader = RequestHeaders.from(reader);
        final HttpCookie cookies = HttpCookie.from(requestHeader.getHeaderValue("Cookie"));
        final RequestBody requestBody = readRequestBody(reader, requestHeader);

        return new HttpRequest(requestLine, requestHeader, cookies, requestBody);
    }

    private static RequestBody readRequestBody(final BufferedReader reader, final RequestHeaders headers)
            throws IOException {
        final String contentLength = headers.getHeaderValue("Content-Length");
        if (contentLength != null) {
            return RequestBody.of(reader, contentLength);
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getHeaderValue(final String header) {
        return requestHeaders.getHeaderValue(header);
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public String getRequestBody() {
        return requestBody.getContent();
    }
}
