package nextstep.jwp.infrastructure.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final ControllerMapping controllerMapping;
    private final ViewResolver viewResolver;

    public RequestHandler(final Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        this.controllerMapping = new ControllerMapping();
        this.viewResolver = new ViewResolver("static");
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest request = HttpRequest.of(splitFromInputStream(bufferedReader));
            final View view = findViewByRequest(request);
            final HttpResponse httpResponse = viewResolver.resolve(view);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (IllegalArgumentException exception) {
            log.error("Exception occurs", exception);
        } finally {
            close();
        }
    }

    private View findViewByRequest(final HttpRequest request) {
        if (controllerMapping.contains(request)) {
            return controllerMapping.handle(request);
        }
        return View.buildByResource(request.getRequestLine().getUri().getBaseUri());
    }

    private List<String> splitFromInputStream(final BufferedReader bufferedReader) throws IOException {
        final List<String> splitResult = new ArrayList<>();
        String line;

        while (Objects.nonNull(line = bufferedReader.readLine()) && bufferedReader.ready()) {
            splitResult.add(line);
        }

        log.debug(String.valueOf(splitResult.size()));
        return splitResult;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
