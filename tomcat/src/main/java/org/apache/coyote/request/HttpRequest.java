package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Optional;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpHeaderType;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean pointsTo(HttpMethod httpMethod, String path) {
        return httpMethod == requestLine.getMethod()
               && path.equals(requestLine.getPath());
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean hasSession() {
        return findCookie()
                .map(HttpCookie::hasSession)
                .orElse(false);
    }

    public String getSession() {
        return findCookie()
                .orElseThrow(() -> new UncheckedServletException("쿠키가 존재하지 않습니다."))
                .getSession();
    }

    private Optional<HttpCookie> findCookie() {
        return httpHeader.find(HttpHeaderType.COOKIE.getName())
                .map(HttpCookie::new);
    }
}
