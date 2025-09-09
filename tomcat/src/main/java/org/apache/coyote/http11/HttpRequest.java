package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> query;
    private final byte[] body;
    private final Map<String, String> form;

    public HttpRequest(
            String method,
            String uri,
            String version,
            Map<String, String> headers,
            Map<String, String> query,
            byte[] body,
            Map<String, String> form
    ) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.headers = headers;
        this.query = query;
        this.body = body;
        this.form = form;
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
}
