package nextstep.jwp.web;

import nextstep.jwp.web.controller.Controller;
import nextstep.jwp.web.controller.ControllerFactory;
import nextstep.jwp.web.controller.ControllerMapping;
import nextstep.jwp.web.exception.InputException;
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

            final HttpRequest request  = new HttpRequest(inputStream);
            final HttpResponse response = new HttpResponse();

            final ControllerMapping controllerMapping = new ControllerMapping(ControllerFactory.create());
            final Controller controller = controllerMapping.findByResource(request.getPath());
            controller.service(request, response);

            deliverResponse(outputStream, response);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (InputException exception) {
            log.info(exception.getMessage());
        } finally {
            close();
        }
    }

    private void deliverResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(response.print());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
