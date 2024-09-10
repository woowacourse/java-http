package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpResponse {

    private HttpResponseHeader headers;
    private HttpStatusCode statusCode;
    private String body;

    private HttpResponse() {
        this.headers = new HttpResponseHeader();
    }

    public static HttpResponse builder() {
        return new HttpResponse();
    }

    public HttpResponse statusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse responseBody(String body) {
        setDefaultHaders(HttpContentType.TEXT, body);
        this.body = body;
        return this;
    }

    private void setDefaultHaders(HttpContentType httpContentType, String body) {
        this.headers.addHeader("Content-Type", "text/html;charset=utf-8");
        this.headers.addHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    public HttpResponse staticResource(String path) {
        StaticResourceLoader loader = new StaticResourceLoader();
        String resource = loader.load(path);
        if (resource.isEmpty()) {
            String notFoundResource = loader.load("/404.html");
            return this.statusCode(HttpStatusCode.NOT_FOUND)
                    .responseBody(notFoundResource);
        }

        HttpContentType contentType = HttpContentType.matchContentType(path);
        setDefaultHaders(contentType, resource);
        this.body = resource;
        return this;
    }

    public String build() {
        return String.join("\r\n",
                statusCode.buildOutput(),
                headers.buildOutput(),
                "",
                body
        );
    }

    public HttpResponse redirect(String uri) {
        this.body = "";
        setDefaultHaders(HttpContentType.TEXT, "");
        headers.addHeader("Location", uri);
        return this;
    }

    public HttpResponse createSession(String name, Object object) {
        HttpCookies cookies = new HttpCookies();
        Session session = new Session();
        session.store(name, object);
        String sessionId = SessionManager.getInstance().storeSession(session);
        cookies.addSession(sessionId);
        this.headers.addHeader("Set-Cookie", cookies.buildOutput());
        return this;
    }
}
