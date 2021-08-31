package nextstep.jwp.infrastructure;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.exception.NoMatchingControllerException;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private BufferedReader inputStreamReader;

    public RequestHandler(final Socket connection, final RequestMapping requestMapping) {
        this.connection = Objects.requireNonNull(connection);
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpResponse httpResponse = getHttpResponse(inputStream);
            final String responseMessage = httpResponse.toResponseMessage();

            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private HttpResponse getHttpResponse(final InputStream inputStream) throws IOException {
        inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.parseRequest(inputStreamReader);
        Controller controller = requestMapping.findController(httpRequest);
        try {
            return controller.doService(httpRequest);
        } catch (ResourceNotFoundException exception) {
            httpRequest = HttpRequest.ofStaticFile("/404.html");
            return requestMapping.getStaticResourceController().doService(httpRequest);
        } catch (RuntimeException exception) {
            httpRequest = HttpRequest.ofStaticFile("/500.html");
            return requestMapping.getStaticResourceController().doService(httpRequest);
        }
    }

    private void close() {
        try {
            inputStreamReader.close();
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
