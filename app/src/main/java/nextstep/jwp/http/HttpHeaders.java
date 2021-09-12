package nextstep.jwp.http;

import com.google.common.base.Strings;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;
    private final HttpCookie httpCookie;

    public HttpHeaders(Map<String, String> headers, HttpCookie httpCookie) {
        this.headers = headers;
        this.httpCookie = httpCookie;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }

    public boolean hasBody() {
        final String content = headers.get("Content-Length");
        return Strings.isNullOrEmpty(content);
    }

    public String getHeaderBy(String headerKey) {
        return headers.get(headerKey);
    }
}
