package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerContainer;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.util.RequestBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = RequestBinder.createRequestByMessage(bufferedReader);
            Controller controller = ControllerContainer.findController(httpRequest);

            HttpResponse httpResponse = controller.doService(httpRequest);
            outputStream.write(httpResponse.toResponseMessage().getBytes());
            outputStream.flush();
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
