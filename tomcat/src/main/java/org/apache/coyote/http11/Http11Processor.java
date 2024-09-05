package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.controller.LoginController;
import com.techcourse.exception.DashboardException;
import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final LoginController loginController = new LoginController();
    private final Http11Helper http11Helper = Http11Helper.getInstance();

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
        try (
                final var inputStream = connection.getInputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final var outputStream = connection.getOutputStream()
        ) {
            StringBuilder request = http11Helper.getRequest(bufferedReader);
            String endpoint = http11Helper.extractEndpoint(request.toString());
            log.info("Requested endpoint: {}", endpoint);

            String response;
            try {
                if (endpoint.startsWith("/login") && endpoint.contains("?")) {
                    response = loginController.login(request.toString());
                } else {
                    String fileName = http11Helper.getFileName(endpoint);
                    response = http11Helper.createResponse(HttpStatus.OK, fileName);
                }
            } catch (UncheckedServletException e) {
                log.error("Error processing request for endpoint: {}", endpoint, e);

                response = http11Helper.createResponse(HttpStatus.NOT_FOUND, "404.html");
            } catch (UnauthorizedException e) {
                log.error("Error processing request for endpoint: {}", endpoint, e);

                response = http11Helper.createResponse(HttpStatus.UNAUTHORIZED, "401.html");
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
