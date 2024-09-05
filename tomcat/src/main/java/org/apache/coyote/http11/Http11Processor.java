package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.executor.ExecutorService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ExecutorService executor;
    private final SessionManager sessionManager = new SessionManager();


    public Http11Processor(final Socket connection, final ExecutorService executor) {
        this.connection = connection;
        this.executor = executor;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
            setSession(httpRequest);
            log.info("요청 [HTTP 메소드: {}, 경로: {}", httpRequest.getMethod(), httpRequest.getPath());

            final HttpResponse response = executor.execute(httpRequest);

            log.info("응답 [HTTP 결과: {}, 헤더: {}", response.getStatusLine(), response.getHeaders());

            HttpResponseWriter.write(outputStream, response);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setSession(final HttpRequest httpRequest) {

        sessionManager.findSession(httpRequest.getCookie(Cookies.SESSION_ID))
                .ifPresentOrElse(httpRequest::setSession,
                        () -> {
                            final Session session = new Session();
                            sessionManager.add(session);
                            httpRequest.setSession(session);
                        });
    }
}
