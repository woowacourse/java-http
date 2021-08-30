package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.HttpResponse;
import nextstep.jwp.model.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String NEW_LINE = "\r\n";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final HttpRequest httpRequest = Converter.convertToHttpRequest(reader);
            final Controller controller = RequestMapping.getController(httpRequest);
            final HttpResponse httpResponse = controller.service(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception exception) {
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
