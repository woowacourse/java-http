package nextstep.jwp;

import nextstep.jwp.http.HttpResponseBuilder;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String message = StringUtils.convertToString(bufferedReader);
            HttpRequestMessage httpRequestMessage = new HttpRequestMessage(message);

            log.debug(String.format("%nRequestLog%n%s%n", httpRequestMessage));

            HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(httpRequestMessage);
            HttpResponseMessage httpResponseMessage = httpResponseBuilder.build();

            log.debug(String.format("%nResponseLog%n%s%n", httpResponseMessage));

            outputStream.write(httpResponseMessage.toBytes());
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
