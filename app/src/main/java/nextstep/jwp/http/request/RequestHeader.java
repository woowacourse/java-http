package nextstep.jwp.http.request;

import java.util.Map;
import nextstep.jwp.http.HttpCookie;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        if (!headers.containsKey("Content-Length")) {
            return 0;
        }
        return Integer.parseInt(headers.get("Content-Length"));
    }

    public HttpCookie getCookie() {
        String line = this.headers.get("Cookie");
        return new HttpCookie(line);
    }
}
