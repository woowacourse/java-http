package nextstep.jwp.http.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(headers.get("Content-Length"), "0");
        return Integer.parseInt(contentLength);
    }
}
