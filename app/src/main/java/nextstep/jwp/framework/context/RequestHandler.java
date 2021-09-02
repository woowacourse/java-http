package nextstep.jwp.framework.context;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import nextstep.jwp.framework.http.*;
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
            doService(inputStream, outputStream);
        } catch (IOException ioException) {
            log.error("IOException", ioException);
        } finally {
            close();
        }
    }

    private void doService(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            doDispatch(inputStream, outputStream);
        } catch (IOException ioException) {
            throw ioException;
        } catch (Exception exception) {
            log.error("Exception", exception);

            final String requestLine = new RequestLine(HttpMethod.GET, "/500.html", HttpVersion.HTTP_1_1).toString();
            doDispatch(new ByteArrayInputStream(requestLine.getBytes()), outputStream);
        }
    }

    private void doDispatch(InputStream inputStream, OutputStream outputStream) throws IOException {
        final HttpRequest httpRequest = HttpRequest.from(inputStream);
        final Controller controller = ControllerMapping.findController(httpRequest);
        final HttpResponse httpResponse = controller.handle(httpRequest);

        log.info(MESSAGE_LOG_FORMAT, httpRequest.readAfterExceptBody(), httpResponse.readAfterExceptBody());

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(httpResponse.readAsString());
        writer.flush();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
