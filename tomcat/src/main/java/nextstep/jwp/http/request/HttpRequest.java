package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    public HttpRequest(final RequestLine requestLine,
                       final RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);

        return new HttpRequest(requestLine, requestHeaders);
    }

    public RequestUri getUri() {
        return requestLine.getRequestUri();
    }

    public RequestMethod getMethod() {
        return requestLine.getRequestMethod();
    }

    public String getUriParameter(String parameter) {
        return requestLine.getUriParameter(parameter);
    }
}
