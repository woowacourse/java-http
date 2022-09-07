package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.RequestHandler;
import nextstep.jwp.presentation.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final HttpRequest httpRequest = HttpRequest.of(bufferedReader.readLine(), getHeaderLines(bufferedReader));
            final Controller controller = RequestHandler.from(httpRequest.getRequestURL().getPath());
            final HttpResponse httpResponse = getHttpResponse(httpRequest, controller);

            String responseMessage = httpResponse.getMessage();
            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getHttpResponse(HttpRequest httpRequest, Controller controller) throws IOException {
        try {
            HttpResponse process = controller.process(httpRequest);
            return process;
        } catch (RuntimeException e) {
            return HttpResponse.notFound();
        }
    }

    private List<String> getHeaderLines(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        return headerLines;
    }
}
