package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.HandlerFactory;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.utils.RequestAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOGGER.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {
            Request request = RequestAssembler.assemble(inputStream);
            Handler handler = HandlerFactory.handler(request);
            Response response = handler.message();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            LOGGER.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOGGER.error("Exception closing socket", exception);
        }
    }
}
