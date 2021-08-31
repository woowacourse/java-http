package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.utils.RequestAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public RequestHandler(Socket connection, RequestMapping requestMapping) {
        this.connection = Objects.requireNonNull(connection);
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        LOGGER.debug("New Client Connect! Connected IP : {}, Port : {}",
                connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {
            Request request = RequestAssembler.assemble(inputStream);
            Controller controller = requestMapping.getController(request.getPath());
            Response response = controller.doService(request);
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
