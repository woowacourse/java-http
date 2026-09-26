package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public abstract class AbstractController implements Controller {
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public final HttpResponse service(HttpRequest request) throws IOException {
        HttpCookie cookie = HttpCookie.from(request.getHeader("cookie"));
        Session session = SessionManager.getSession(cookie.get(JSESSIONID).orElse(null));
        String method = request.getMethod();

        if (!getAllowedMethods().contains(method)) {
            return methodNotAllowedResponse(request, session);
        }

        if ("GET".equals(method)) {
            return doGet(request, session);
        }

        if ("POST".equals(method)) {
            return doPost(request, session);
        }

        return methodNotAllowedResponse(request, session);
    }

    protected abstract List<String> getAllowedMethods();

    protected HttpResponse doGet(HttpRequest request, Session session) throws IOException {
        throw new IllegalStateException("GET을 지원하지만 doGet이 구현되지 않았습니다.");
    }

    protected HttpResponse doPost(HttpRequest request, Session session) throws IOException {
        throw new IllegalStateException("POST를 지원하지만 doPost가 구현되지 않았습니다.");
    }

    private HttpResponse methodNotAllowedResponse(HttpRequest request, Session session) {
        Map<String, String> headers = new LinkedHashMap<>();
        setCookieHeader(headers, request, session);
        headers.put("Allow", String.join(", ", getAllowedMethods()));
        headers.put("Content-Length", "0");

        return new HttpResponse(
                new StatusLine("HTTP/1.1", "405", "Method Not Allowed"),
                new HttpHeaders(headers),
                new byte[0]
        );
    }

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
