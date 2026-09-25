package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public abstract class AbstractController implements Controller {
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public final HttpResponse service(HttpRequest request) throws IOException {
        HttpCookie cookie = HttpCookie.from(request.getHeader("cookie"));
        Session session = SessionManager.getSession(cookie.get(JSESSIONID).orElse(null));

        if ("GET".equals(request.getMethod())) {
            return doGet(request, session);
        }

        if ("POST".equals(request.getMethod())) {
            return doPost(request, session);
        }

        throw new IOException("지원하지 않는 메소드입니다. : " + request.getMethod());
    }

    protected abstract HttpResponse doGet(HttpRequest request, Session session) throws IOException;

    protected abstract HttpResponse doPost(HttpRequest request, Session session) throws IOException;

    protected final HttpResponse resourceResponse(HttpRequest request, Session session, String resourcePath)
            throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("리소스를 찾을 수 없습니다. : " + resourcePath);
            }

            byte[] body = resource.readAllBytes();
            Map<String, String> headers = new LinkedHashMap<>();
            setCookieHeader(headers, request, session);
            headers.put("Content-Type", URLConnection.guessContentTypeFromName(resourcePath) + ";charset=utf-8");
            headers.put("Content-Length", body.length + "");

            return new HttpResponse(
                    new StatusLine("HTTP/1.1", "200", "OK"),
                    new HttpHeaders(headers),
                    body
            );
        }
    }

    protected final HttpResponse redirectResponse(HttpRequest request, Session session, String location) {
        Map<String, String> headers = new LinkedHashMap<>();
        setCookieHeader(headers, request, session);
        headers.put("Location", location);
        headers.put("Content-Length", "0");

        return new HttpResponse(
                new StatusLine("HTTP/1.1", "302", "Found"),
                new HttpHeaders(headers),
                new byte[0]
        );
    }

    private void setCookieHeader(Map<String, String> headers, HttpRequest request, Session session) {
        HttpCookie cookie = HttpCookie.from(request.getHeader("cookie"));
        boolean sameSession = cookie.get(JSESSIONID)
                .filter(session.getId()::equals)
                .isPresent();

        if (!sameSession) {
            headers.put("Set-Cookie", JSESSIONID + "=" + session.getId());
        }
    }
}
