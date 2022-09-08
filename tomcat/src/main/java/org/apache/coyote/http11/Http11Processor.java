package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.frontcontroller.FrontController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest httpRequest = HttpRequest.of(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(outputStream);
            setSession(httpRequest, httpResponse);

            FrontController frontController = new FrontController();
            frontController.doDispatch(httpRequest, httpResponse);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setSession(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String cookieValue = httpRequest.getCookieValue();
        Cookie cookie = Cookie.of(cookieValue);
        Manager sessionManager = new SessionManager();

        if (!cookie.hasJSessionId()) {
             UUID uuid = UUID.randomUUID();
            httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + uuid);
            Session session = new Session(uuid.toString());
            sessionManager.add(session);
            httpRequest.setSession(session);
            return;
        }
        Session session = getSession(cookie, sessionManager);
        httpRequest.setSession(session);
    }

    private Session getSession(Cookie cookie, Manager sessionManager) throws IOException {
        Session session = sessionManager.findSession(cookie.getSessionKey());

        if (session == null) {
            session = new Session(cookie.getSessionKey());
            sessionManager.add(session);
        }
        return session;
    }
}
