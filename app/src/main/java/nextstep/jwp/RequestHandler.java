package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.adaptor.HandlerAdaptorImpl;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import nextstep.jwp.handler.ResourceHandler;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mapper.HandlerMapperImpl;
import nextstep.jwp.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Dispatcher dispatcher;

    public RequestHandler(Socket connection, Assembler assembler) {
        this.connection = Objects.requireNonNull(connection);
        this.dispatcher = assembler.dispatcher();
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequest.of(inputStream);
            final HttpResponse httpResponse = dispatcher.execute(httpRequest);

            outputStream.write(httpResponse.responseAsBytes());
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
