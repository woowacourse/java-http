package org.apache.coyote.http11.message.request;

import java.net.URI;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.util.parser.BodyParserFactory;
import org.apache.util.parser.Parser;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(HttpRequestLine requestLine, HttpHeaders headers, HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public String getBodyParameter(String key) {
        Map<String, String> keyValueBodies = this.getKeyValueBodies();
        return keyValueBodies.get(key);
    }

    private Map<String, String> getKeyValueBodies() {
        ContentType contentType = this.getContentType();
        Parser parser = BodyParserFactory.getParser(contentType);

        return parser.parse(this.getBody());
    }

    public ContentType getContentType() {
        return headers.getContentType();
    }

    public String getBody() {
        return body.getBody();
    }

    public URI getUri() {
        return requestLine.getUri();
    }

    public boolean hasPath(String path) {
        return requestLine.hasPath(path);
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Session getSession() {
        HttpCookies cookies = getCookies();
        HttpCookie cookie = cookies.getCookie(Session.JSESSIONID);
        Session session = SessionManager.getInstance().findSession(cookie.getValue());

        if (session == null) {
            return new Session();
        }
        return session;
    }
}
