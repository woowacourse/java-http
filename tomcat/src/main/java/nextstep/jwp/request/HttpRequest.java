package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpVersion;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders) {
        this(requestLine, requestHeaders, null);
    }

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final InputStream inputStream) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        final RequestHeaders requestHeader = RequestHeaders.of(bufferedReader);
        if (requestHeader.getHeaderValue("Content-Length") != null) {
            final RequestBody requestBody = RequestBody.of(bufferedReader,
                    requestHeader.getHeaderValue("Content-Length"));
            return new HttpRequest(requestLine, requestHeader, requestBody);
        }

        return new HttpRequest(requestLine, requestHeader);
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

    public String getRequestBody() {
        return requestBody.getContent();
    }
}
