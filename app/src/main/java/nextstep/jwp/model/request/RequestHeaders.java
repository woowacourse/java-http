package nextstep.jwp.model.request;

import java.util.Map;

public class RequestHeaders {

    public static final String COOKIE = "Cookie";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> values;

    public RequestHeaders(Map<String, String> values) {
        this.values = values;
    }

    public Cookie getCookie() {
        if (hasCookie()) {
            return new Cookie(values.get(COOKIE));
        }
        return new Cookie();
    }

    public boolean hasCookie() {
        return values.containsKey(COOKIE);
    }

    public boolean hasContentLength() {
        return values.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(values.get(CONTENT_LENGTH));
    }
}
