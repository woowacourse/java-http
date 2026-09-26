package org.apache.coyote.http11;


import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.HttpSession;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.catalina.Controller;
import org.apache.catalina.ControllerResolver;
import org.apache.catalina.SessionResolver;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final SessionResolver sessionResolver;
    private final ControllerResolver controllerResolver;

    public Http11Processor(final Socket connection, final SessionResolver sessionResolver, final ControllerResolver controllerResolver) {
        this.connection = connection;
        this.sessionResolver = sessionResolver;
        this.controllerResolver = controllerResolver;
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

            HttpRequest request = HttpRequest.parse(inputStream, sessionResolver);
            HttpResponse response = new HttpResponse();

            Controller controller = controllerResolver.getController(request);

            controller.service(request, response);

            addSessionCookieIfNeeded(request, response);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addSessionCookieIfNeeded(HttpRequest request, HttpResponse response) {

        if (response.hasHeader("Set-Cookie")) {
            return;
        }

        if (sessionResolver.hasValidSession(request.getHeaders())) {
            return;
        }

        HttpSession session = request.getSession(true);

        if (session == null) {
            return;
        }

        String sessionId = sessionResolver.getSessionId(session);
        HttpCookie cookie = new HttpCookie(sessionId);

        response.addHeader(
                "Set-Cookie",
                List.of(cookie.toString())
        );
    }
}
