package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpResponse {
    private static final String CONTENT_TYPE_FORMAT = "%s;charset=utf-8";

    private final OutputStream outputStream;
    private HttpResponseHeader headers;
    private HttpStatusCode statusCode;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HttpResponseHeader();
    }

    public HttpResponse statusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public void responseBody(String body) {
        setDefaultHaders(HttpContentType.TEXT, body);
        write(body);
    }

    private void write(String responseBody) {
        try (outputStream) {
            outputStream.write(build(responseBody).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String build(String responseBody) {
        return String.join("\r\n",
                statusCode.buildOutput(),
                headers.buildOutput(),
                "",
                responseBody
        );
    }

    private void setDefaultHaders(HttpContentType httpContentType, String body) {
        this.headers.addHeader("Content-Type", CONTENT_TYPE_FORMAT.formatted(httpContentType.getContentType()));
        this.headers.addHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    public void staticResource(String path) {
        StaticResourceLoader loader = new StaticResourceLoader();
        String resource = loader.load(path);
        if (resource.isEmpty()) {
            String notFoundResource = loader.load("/404.html");
            this.statusCode(HttpStatusCode.NOT_FOUND)
                    .responseBody(notFoundResource);
        }

        HttpContentType contentType = HttpContentType.matchContentType(path);
        setDefaultHaders(contentType, resource);
        write(resource);
    }

    public void redirect(String uri) {
        headers.addHeader("Location", uri);
        setDefaultHaders(HttpContentType.TEXT, "");
        write("");
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
