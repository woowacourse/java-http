package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            final HttpRequest request = readHttpRequest(bufferedReader);
            final String requestPath = request.getUrl();

            if (requestPath.contains("/login") && requestPath.contains("?")) {
                login(requestPath);
            }

            final ViewResolver viewResolver = new ViewResolver(requestPath);
            final Optional<URI> uri = viewResolver.resolveView();
            final HttpResponse httpResponse = HttpResponse.of(uri);
            final String response = httpResponse.getBody();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();

        final List<String> headerLines = new ArrayList<>();
        while (bufferedReader.ready()) {
            headerLines.add(bufferedReader.readLine());
        }

        return HttpRequest.from(line, headerLines);
    }

    private void login(final String requestPath) {
        final Controller controller = new Controller();
        final Map<String, String> params = QueryStringParser.parsing(requestPath);
        controller.checkLogin(params);
    }
}
