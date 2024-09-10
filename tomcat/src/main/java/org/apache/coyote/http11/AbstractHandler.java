package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.session.SimpleSession;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractHandler {

    public abstract boolean canHandle(HttpRequest httpRequest);

    protected abstract ForwardResult forward(HttpRequest httpRequest, Manager sessionManager);

    public HttpResponse handle(HttpRequest httpRequest, Manager sessionManager) {
        ForwardResult result = forward(httpRequest, sessionManager);
        String resourcePath = getClass().getClassLoader().getResource("static/" + result.path()).getPath();
        Header header = result.header();
        String contentType = ContentType.determineContentType(resourcePath).getName();
        header.append(HttpHeaderKey.CONTENT_TYPE, contentType);

        if (result.httpStatus().isRedirection()) {
            return new HttpResponse(httpRequest.httpVersion(), result.httpStatus(), header, new byte[]{});
        }

        return new HttpResponse(
                httpRequest.httpVersion(),
                result.httpStatus(),
                header,
                readStaticResource(resourcePath)
        );
    }

    private byte[] readStaticResource(String resourcePath) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath))) {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Cookie createCookie(HttpRequest httpRequest) {
        return httpRequest.header()
                .get(HttpHeaderKey.COOKIE)
                .map(Cookie::new)
                .orElse(new Cookie(""));
    }

    protected HttpSession findSessionOrCreate(Manager sessionManager, Cookie cookie) {
        String id = cookie.get(getSessionKey()).orElse("");
        if (id.isBlank()) {
            HttpSession session = new SimpleSession();
            sessionManager.add(session);
            return session;
        }

        try {
            return sessionManager.findSession(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getSessionKey() {
        return "JSESSIONID";
    }
}
