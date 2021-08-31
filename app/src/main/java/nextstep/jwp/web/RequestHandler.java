package nextstep.jwp.web;

import nextstep.jwp.web.controller.Controller;
import nextstep.jwp.web.controller.ControllerFactory;
import nextstep.jwp.web.controller.ControllerMapping;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            final HttpRequest httpRequest  = new HttpRequest(inputStream);
            final HttpResponse httpResponse = new HttpResponse();
            final ControllerMapping controllerMapping = new ControllerMapping(ControllerFactory.create());
            final Controller mappedController = controllerMapping.findByResource(httpRequest.getPath());
            mappedController.service(httpRequest, httpResponse);

            bufferedWriter.write(httpResponse.print());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
