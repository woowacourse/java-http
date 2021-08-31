package nextstep.jwp.http;

import nextstep.jwp.controller.*;
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
    private static final Controller staticResourceController = new StaticResourceController();
    private static final List<Controller> controllers = new ArrayList<>();

    private final Socket connection;
    private BufferedReader inputStreamReader;

    static {
        final UserService userService = new UserService();
        controllers.add(new LoginController(userService));
        controllers.add(new RegisterController(userService));
        controllers.add(staticResourceController);
    }

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
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
        final Controller controller = findController(httpRequest);
        try {
            return controller.doService(httpRequest);
        } catch (ResourceNotFoundException exception) {
            httpRequest = HttpRequest.ofStaticFile("/404.html");
            return staticResourceController.doService(httpRequest);
        } catch (RuntimeException exception) {
            httpRequest = HttpRequest.ofStaticFile("/500.html");
            return staticResourceController.doService(httpRequest);
        }
    }

    private Controller findController(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(it -> it.canHandle(httpRequest))
                .findAny()
                .orElseThrow(NoMatchingControllerException::new);
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
