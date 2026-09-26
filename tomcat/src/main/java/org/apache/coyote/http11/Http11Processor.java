package org.apache.coyote.http11;

import com.techcourse.web.RequestMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.catalina.Session;
import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID=";

    private final Socket connection;

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
             final var outputStream = connection.getOutputStream();
             final var inputReader = new InputStreamReader(inputStream);
             final var reader = new BufferedReader(inputReader)) {

            HttpRequest request = HttpRequest.from(reader);
            HttpResponse response = new HttpResponse();

            HttpCookie cookie = request.getCookie();
            Session session = request.getSession(true);
            if (!cookie.hasJSessionId()) {
                response.addHeader(SET_COOKIE, JSESSIONID + session.getId());
            }

            Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);

            outputStream.write(response.toHttpMessage().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
