package nextstep.jwp.http.request;

import java.util.Map;

public class RequestHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH)) {
            String value = headers.get(CONTENT_LENGTH);
            return Integer.parseInt(value);
        }
        return 0;
    }
}
