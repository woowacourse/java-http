package nextstep.jwp.http;

import nextstep.jwp.controller.*;
import nextstep.jwp.exception.NoMatchingControllerException;
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
    private static final List<Controller> controllers = new ArrayList<>();

    private final Socket connection;
    private BufferedReader inputStreamReader;

    static {
        final UserService userService = new UserService();
        controllers.add(new LoginController(userService));
        controllers.add(new RegisterController(userService));
        controllers.add(new StaticResourceController());
    }

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = parseRequest(inputStream);

            final Controller controller = findController(httpRequest);
            final HttpResponse httpResponse = controller.doService(httpRequest);
            final String responseMessage = httpResponse.toResponseMessage();

            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private HttpRequest parseRequest(final InputStream inputStream) throws IOException {
        inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));

        final List<String> requestHeaders = parseRequestHeaders();
        final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(requestHeaders);

        final String requestBody = parseRequestBody(httpRequestHeader);
        final HttpRequestBody httpRequestBody = new HttpRequestBody(requestBody);

        return new HttpRequest(httpRequestHeader, httpRequestBody);
    }

    private List<String> parseRequestHeaders() throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String line = inputStreamReader.readLine();
        while (!"".equals(line)) {
            requestHeaders.add(line);
            line = inputStreamReader.readLine();
            if (line == null) {
                break;
            }
        }

        return requestHeaders;
    }

    private String parseRequestBody(final HttpRequestHeader httpRequestHeader) throws IOException {
        int contentLength = httpRequestHeader.getContentLength();
        char[] buffer = new char[contentLength];
        inputStreamReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private Controller findController(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(it -> it.canHandle(httpRequest))
                .findFirst()
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
