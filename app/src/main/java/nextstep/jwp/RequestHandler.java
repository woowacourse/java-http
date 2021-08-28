package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NoneMatchingControllerException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpRequestHeader;
import nextstep.jwp.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.beancontext.BeanContext;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final List<Controller> controllers = new ArrayList<>();

    private final Socket connection;
    private BufferedReader inputStreamReader;

    static {
        controllers.add(new IndexController());
        controllers.add(new LoginController());
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
        final List<String> requestHeaders = parseRequestHeaders(inputStream);
        final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(requestHeaders);
        //TODO: request body parsing 추가

        return new HttpRequest(httpRequestHeader);
    }

    private List<String> parseRequestHeaders(final InputStream inputStream) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
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

    private Controller findController(final HttpRequest httpRequest) {
        return controllers.stream()
                .filter(contoller -> contoller.canHandle(httpRequest))
                .findFirst()
                .orElseThrow(NoneMatchingControllerException::new);
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
