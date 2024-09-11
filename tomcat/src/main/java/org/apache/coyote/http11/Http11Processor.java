package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var httpRequest = HttpRequest.from(bufferedReader);
            log.info("요청 = {}", httpRequest.getRequestLine());
            final var httpResponse = new HttpResponse();
            final var loginController = new LoginController();

            if (httpRequest.hasPath("/login")) {
                loginController.service(httpRequest, httpResponse);
            } else if (httpRequest.hasPath("/register")) {
                final var user = new User(httpRequest.getBodyValue("account"),
                        httpRequest.getBodyValue("password"),
                        httpRequest.getBodyValue("email"));
                InMemoryUserRepository.save(user);
                redirectLocation(httpResponse, httpRequest, "index.html");
            } else {
                httpResponse.setHttpStatusCode(HttpStatusCode.OK);
                httpResponse.setSourceCode(httpRequest.getResources());
                httpResponse.putHeader("Content-Length", httpRequest.getContentLength());
                httpResponse.putHeader("Content-Type", httpRequest.getContentTypeToResponseText());
            }

            outputStream.write(httpResponse.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void redirectLocation(final HttpResponse response, final HttpRequest request,
                                  final String location) throws IOException {
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.setSourceCode(request.getResources());
        response.putHeader("Content-Length", request.getContentLength());
        response.putHeader("Content-Type", request.getContentTypeToResponseText());
        response.putHeader("Location", location);
    }
}
