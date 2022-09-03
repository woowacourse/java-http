package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
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
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = toHttpRequest(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();

            doService(httpRequest, httpResponse);

            write(outputStream, httpResponse);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest toHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = readHttpRequest(bufferedReader);
        return HttpRequest.from(rawHttpRequest);
    }

    private List<String> readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> rawHttpRequest = new ArrayList<>();

        String line = " ";
        while (!line.isEmpty()) {
            line = bufferedReader.readLine();
            rawHttpRequest.add(line);
        }

        log.info("============= HTTP REQUEST =============");
        log.info(String.join("\n", rawHttpRequest));

        return rawHttpRequest;
    }

    private void doService(final HttpRequest request, final HttpResponse response) throws Exception {
        final String path = request.getPath();
        final Controller controller = RequestMapping.findController(path);
        controller.service(request, response);
    }

    private void write(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
