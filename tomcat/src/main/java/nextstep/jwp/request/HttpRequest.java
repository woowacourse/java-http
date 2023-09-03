package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private RequestLine requestLine;
    private RequestHeaders requestHeaders;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
    }

    public static HttpRequest of(final InputStream inputStream) throws IOException {
        final InputStreamReader reader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        final RequestHeaders requestHeader = RequestHeaders.of(bufferedReader);

        return new HttpRequest(requestLine, requestHeader);
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }
}
