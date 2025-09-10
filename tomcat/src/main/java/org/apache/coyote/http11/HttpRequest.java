package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> query;
    private final byte[] body;
    private final Map<String, String> form;
    private HttpCookie httpCookie;

    public HttpRequest(
            String method,
            String uri,
            String version,
            Map<String, String> headers,
            Map<String, String> query,
            byte[] body,
            Map<String, String> form,
            HttpCookie httpCookie
    ) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.headers = headers;
        this.query = query;
        this.body = body;
        this.form = form;
        this.httpCookie = httpCookie;
    }

    public String method() {
        return method;
    }

    public String uri() {
        return uri;
    }

    public String version() {
        return version;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public byte[] body() {
        return body;
    }

    public String header(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getStringBody() {
        return new String(body, UTF_8);
    }

    public String getForm(String key) {
        return form.get(key);
    }

    public Session getSession() {
        if (httpCookie.get("SID") == null) {
            return SessionManager.createNew();
        }
        return SessionManager.findSession(httpCookie.get("SID"));
    }
}
