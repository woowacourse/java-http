package org.apache.coyote.http11;

import static org.apache.coyote.http11.RequestMethod.GET;
import static org.apache.coyote.http11.RequestMethod.valueOf;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final LoginController loginController = new LoginController();

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
            HttpRequest httpRequest = new HttpRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);
            RequestMethod requestMethod = valueOf(httpRequest.getRequestMethod());
            String requestUri = httpRequest.getUri();

            if (requestMethod == GET) {
                if (requestUri.equals("/login") && httpRequest.getQuery().isEmpty()) {
                    processGet(requestUri, httpResponse);
                    return;
                }
                if (requestUri.equals("/login")) {
                    loginController.login(httpRequest, httpResponse);
                }
                processGet(requestUri, httpResponse);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processGet(
            final String uri,
            final HttpResponse httpResponse
    ) throws IOException {
        httpResponse.sendResponse(uri);
    }
}
