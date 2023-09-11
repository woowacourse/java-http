package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.apache.catalina.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;
    private static final Manager manager = new SessionManager();

    public HttpRequest(
            HttpRequestHeader httpRequestHeader,
            HttpRequestBody httpRequestBody
    ) {
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public HttpMethod getMethod() {
        return httpRequestHeader.getMethod();
    }

    public String getPath() {
        return httpRequestHeader.getPath();
    }

    public String getBodyAttribute(String key) {
        return httpRequestBody.get(key);
    }

    public HttpSession getHttpSession() {
        return getHttpSession(false);
    }

    public HttpSession getHttpSession(boolean create) {
        String jsessionId = httpRequestHeader.getCookies()
                .get("JSESSIONID");

        HttpSession session = null;

        try {
            session = manager.findSession(jsessionId);
        } catch (IOException e) {
            // ignored
        }

        if (create && Objects.isNull(session)) {
            String uuid = UUID.randomUUID()
                    .toString();
            session = new HttpSession(uuid);
            manager.add(session);
            return session;
        }

        if (create) {
            session.invalidate();
            manager.remove(session);
            String uuid = UUID.randomUUID()
                    .toString();
            session = new HttpSession(uuid);
            manager.add(session);
        }

        return session;
    }

}
