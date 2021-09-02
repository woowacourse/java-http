package nextstep.jwp.http;

import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

            HttpTranslator httpTranslator = new HttpTranslator(inputStream, outputStream);
            HttpRequestMessage httpRequestMessage = httpTranslator.translate();

            log.debug(httpRequestMessage.toString());

            HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(httpRequestMessage);
            HttpResponseMessage httpResponseMessage = httpResponseBuilder.build();
            httpTranslator.respond(httpResponseMessage);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            log.error("Exception", e.getMessage());
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
