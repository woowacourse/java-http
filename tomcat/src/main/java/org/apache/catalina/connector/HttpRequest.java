package org.apache.catalina.connector;

import java.util.Objects;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.tomcat.util.http.HttpMethod;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpCookie;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.apache.tomcat.util.http.parser.HttpCookieParser;

public record HttpRequest(HttpRequestLine requestLine, HttpHeaders httpHeaders, HttpBody httpBody) {

    public ResourceURI getURI() {
        return requestLine.resourceURI();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.httpMethod();
    }

    public HttpCookie getCookie() {
        return new HttpCookie(HttpCookieParser.parseCookies(httpHeaders.get(HttpHeaderType.COOKIE)));
    }

    public Session getSession(boolean create) {
        String sessionId = getCookie().get(HttpCookie.SESSION_ID_IDENTIFICATION);
        SessionManager sessionManager = new SessionManager();
        Session session = sessionManager.findSession(sessionId);
        if (Objects.nonNull(session)) {
            return session;
        }
        if (create) {
            return new Session();
        }
        return null;
    }
}
