package nextstep.jwp;

import nextstep.jwp.core.mvc.FrontHandler;
import nextstep.jwp.webserver.request.DefaultHttpRequest;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.DefaultHttpResponse;
import nextstep.jwp.webserver.response.HttpResponse;
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
    private final FrontHandler frontHandler;

    public RequestHandler(Socket connection, FrontHandler frontHandler) {
        this.connection = Objects.requireNonNull(connection);
        this.frontHandler = frontHandler;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new DefaultHttpRequest(inputStream);
            HttpResponse httpResponse = new DefaultHttpResponse();

            final String response = frontHandler.getResponse(httpRequest, httpResponse).totalResponse();

            outputStream.write(response.getBytes());
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
