package nextstep.jwp.http.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.http.handler.session.SessionHandler;
import nextstep.jwp.http.mapping.RequestMapping;
import nextstep.jwp.http.mapping.SessionMapping;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.util.RequestConverter;
import nextstep.jwp.mvc.controller.Controller;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream))
        ) {
            final HttpRequest request = RequestConverter.convertToHttpRequest(bufferedReader);
            final Controller controller = RequestMapping.getController(request);
            final HttpResponse response = controller.service(request);
            final SessionHandler sessionHandler = SessionMapping.getSessionHandler(request);

            sessionHandler.handle(request, response);

            outputStream.write(response.getResponseByByte());
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
