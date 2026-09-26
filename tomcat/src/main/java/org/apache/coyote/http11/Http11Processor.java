package org.apache.coyote.http11;


import com.techcourse.RequestMapping;
import com.techcourse.controller.Controller;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.HttpSession;
import com.techcourse.resource.StaticResourceLoader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;

    private final SessionManager sessionManager = new SessionManager();
    private final StaticResourceLoader staticResourceLoader = new StaticResourceLoader();
    private final RequestMapping requestMapping = new RequestMapping(sessionManager, staticResourceLoader);

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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

            HttpRequest request = HttpRequest.parse(inputStream, sessionManager);
            HttpResponse response = new HttpResponse();

            Controller controller = requestMapping.getController(request);

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

        if (!request.hasNewSession()) {
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        Session actualSession = (Session) session;
        HttpCookie cookie = new HttpCookie(actualSession.getId());

        response.addHeader(
                "Set-Cookie",
                List.of(cookie.toString())
        );
    }
}
