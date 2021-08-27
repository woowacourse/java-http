package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.RequestAssembler;
import nextstep.jwp.model.Response;
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
            RequestAssembler requestAssembler = new RequestAssembler(inputStream);
            Request request = requestAssembler.assemble();
            Response response = new Response(request);
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
