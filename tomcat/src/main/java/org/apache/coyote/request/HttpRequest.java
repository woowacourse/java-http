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
        if (!hasSession()) {
            throw new UncheckedServletException("세션이 존재하지 않습니다.");
        }

        return findCookie()
                .map(HttpCookie::getSession)
                .orElseThrow(() -> new UncheckedServletException("쿠키가 존재하지 않습니다."));
    }

    private Optional<HttpCookie> findCookie() {
        return httpHeader.find(HttpHeaderType.COOKIE.getName())
                .map(HttpCookie::new);
    }

    public boolean hasMethod(HttpMethod httpMethod) {
        return requestLine.hasMethod(httpMethod);
    }
}
