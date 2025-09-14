package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerContainer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerContainer controllerContainer;

    public Http11Processor(final Socket connection, ControllerContainer controllerContainer) {
        this.connection = connection;
        this.controllerContainer = controllerContainer;
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
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1))
        ) {

            HttpRequest request = RequestParser.parse(bufferedReader);
            HttpResponse response = processHttpRequest(request);
            String responseString = response.toResponseString();

            outputStream.write(responseString.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public HttpResponse processHttpRequest(HttpRequest request) throws Exception {
        String path = request.path();

        Controller controller = controllerContainer.findController(path);
        return controller.service(request);
    }

}
