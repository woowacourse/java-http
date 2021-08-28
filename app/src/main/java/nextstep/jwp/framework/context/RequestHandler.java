package nextstep.jwp.framework.context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpRequestParser;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpResponseWriter;
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

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequestParser(inputStream).parseRequest();
            final Controller controller = ControllerMapping.findController(httpRequest);
            final HttpResponse httpResponse = controller.handle(httpRequest);
            new HttpResponseWriter(httpResponse).writeWith(outputStream);
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
