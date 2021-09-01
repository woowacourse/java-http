package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.requestHeaders = new RequestHeaders(bufferedReader);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getHeader(String key) {
        return requestHeaders.get(key);
    }
}
