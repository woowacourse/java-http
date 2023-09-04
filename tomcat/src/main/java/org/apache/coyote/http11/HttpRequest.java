package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;
    private static final SessionManager sessionManager = new SessionManager();

    public HttpRequest(
            HttpRequestHeader httpRequestHeader,
            HttpRequestBody httpRequestBody
    ) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }

    public HttpSession getHttpSession() {
        String jsessionId = httpRequestHeader.getCookies()
                .get("JSESSIONID");

        try {
            return sessionManager.findSession(jsessionId);
        } catch (IOException e) {
            // ignored
        }

        return null;
    }

}
