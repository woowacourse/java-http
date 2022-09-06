package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpFrontServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_BODY = "Hello world!";

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                StandardCharsets.UTF_8)); final var outputStream = connection.getOutputStream()) {
            final HttpFrontServlet httpRequestServlet = new HttpFrontServlet();

            final HttpRequest httpRequest = HttpRequest.of(readHttpRequest(bufferedReader));
            final ResponseEntity response = handleRequest(httpRequest, httpRequestServlet);
            final HttpResponse httpResponse = HttpResponse.of(response);

            writeResponse(outputStream, httpResponse.createResponse());
        } catch (final IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Queue<String> readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final Queue<String> httpRequest = new LinkedList<>();

        String line = bufferedReader.readLine();
        while (line != null && !line.equals("")) {
            httpRequest.add(line);
            line = bufferedReader.readLine();
        }

        return httpRequest;
    }

    private ResponseEntity handleRequest(final HttpRequest httpRequest, final HttpFrontServlet frontHandler)
            throws IOException {
        final String path = httpRequest.getPath();

        if (isRootPath(path)) {
            return new ResponseEntity(OK, "text/html", "Hello world!");
        }

        if (FileHandler.isStaticFileResource(path)) {
            return FileHandler.createFileResponse(path);
        }

        return frontHandler.service(httpRequest.getPath(), httpRequest);
    }

    private boolean isRootPath(final String path) {
        return path.equals("/");
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
