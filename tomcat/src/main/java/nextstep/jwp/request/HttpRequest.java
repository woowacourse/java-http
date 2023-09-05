package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
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

    public static HttpRequest of(final InputStream inputStream) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        final RequestHeaders requestHeader = RequestHeaders.of(bufferedReader);
        final HttpCookie cookies = HttpCookie.from(requestHeader.getHeaderValue("Cookie"));
        final RequestBody requestBody = readRequestBody(bufferedReader, requestHeader);

        return new HttpRequest(requestLine, requestHeader, cookies, requestBody);
    }

    private static RequestBody readRequestBody(final BufferedReader bufferedReader, final RequestHeaders requestHeader)
            throws IOException {
        if (requestHeader.getHeaderValue("Content-Length") != null) {
            return RequestBody.of(bufferedReader,
                    requestHeader.getHeaderValue("Content-Length"));
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public RequestHeaders getHeaders() {
        return requestHeaders;
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public String getRequestBody() {
        return requestBody.getContent();
    }
}
