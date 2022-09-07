package org.apache.coyote.http11.http11request;

import java.util.Map;
import org.apache.coyote.http11.HeaderElement;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.cookie.HttpCookie;

public class Http11Request {

    private final HttpMethod httpMethod;
    private final String uri;
    private final Map<String, String> header;
    private final String body;

    public Http11Request(String httpMethod, String uri, Map<String, String> header, String body) {
        this.httpMethod = HttpMethod.valueOf(httpMethod.toUpperCase());
        this.uri = uri;
        this.header = header;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public String getSessionId() {
        if (!header.containsKey(HeaderElement.COOKIE.getValue())) {
            return null;
        }
        HttpCookie httpCookie = HttpCookie.of(header.get(HeaderElement.COOKIE.getValue()));
        if (httpCookie.hasJessionId()) {
            return httpCookie.getJsessionId();
        }
        return null;
    }
}
