package nextstep.jwp.webserver;

import nextstep.jwp.application.controller.StaticFileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest;
            HttpResponse httpResponse = new HttpResponse();

            try {
                httpRequest = getHttpRequest(bufferedReader);
                Controller controller = getController(httpRequest);
                controller.service(httpRequest, httpResponse);
            } catch (BaseException baseException) {
                HttpResponse.errorPage(baseException, httpResponse);
                log.error("BaseException", baseException);
            }

            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(httpResponse.readAsString());
            writer.flush();
        } catch (IOException ioException) {
            log.error("Exception stream", ioException);
        } catch (Exception exception) {
            log.error("Exception", exception);
        } finally {
            close();
        }
    }

    private HttpRequest getHttpRequest(BufferedReader bufferedReader) throws IOException {
        return new HttpRequest(bufferedReader);
    }

    private Controller getController(HttpRequest httpRequest) {
        Controller controller = RequestMapping.get(httpRequest.getUri());
        return Objects.requireNonNullElseGet(controller, StaticFileController::new);
    }

    private void close() {
        try {
            connection.close();
            log.debug("Client Connection Close! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
