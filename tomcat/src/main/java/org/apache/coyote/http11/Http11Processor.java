package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_PATH_ATTRIBUTE = "; Path=/";
    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(Socket connection, SessionManager sessionManager) {
        this.connection = connection;
        this.requestMapping = new RequestMapping(sessionManager);
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(bufferedReader);
            HttpResponse response = HttpResponse.create();

            if (request.shouldIssueSessionCookie()) {
                HttpCookie cookie = request.getCookie();
                response.addHeader(SET_COOKIE_HEADER,
                        cookie.getSessionIdCookieName() + COOKIE_KEY_VALUE_SEPARATOR
                                + cookie.getSessionId() + COOKIE_PATH_ATTRIBUTE);
            }

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
