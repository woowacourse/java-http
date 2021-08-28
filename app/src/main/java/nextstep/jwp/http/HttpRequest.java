package nextstep.jwp.http;

import java.util.Map;

public class HttpRequest {
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(final HttpRequestHeader header) {
        this(header, null);
    }

    public HttpRequest(final HttpRequestHeader header, final HttpRequestBody body) {
        this.header = header;
        this.body = body;
    }

    public boolean isGet() {
        return "GET".equals(getHttpMethod());
    }

    public String getHttpMethod() {
        return header.getHttpMethod().toUpperCase();
    }

    public String getPath() {
        return header.getPath();
    }

    public Map<String, String> getQueryParameters() {
        return header.getQueryParameters();
    }

    public String getProtocol() {
        return header.getProtocol();
    }

    public Map<String, String> getPayload() {
        return body.getPayload();
    }
}
