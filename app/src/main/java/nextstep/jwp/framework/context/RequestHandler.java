package nextstep.jwp.framework.context;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public static final String MESSAGE_LOG_FORMAT = "\n\nHTTP REQUEST\n{}\nHTTP RESPONSE\n{}";

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            final Controller controller = ControllerMapping.findController(httpRequest);
            final HttpResponse httpResponse = controller.handle(httpRequest);

            log.info(MESSAGE_LOG_FORMAT, httpRequest.readAfterExceptBody(), httpResponse.readAfterExceptBody());

            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(httpResponse.readAsString());
            writer.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            log.error("Exception", e);
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
