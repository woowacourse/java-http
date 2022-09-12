package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.servlet.HttpFrontServlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;
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
        try (final var bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = createRequest(bufferedReader);
            final ResponseEntity response = handleRequest(httpRequest);
            final HttpResponse httpResponse = HttpResponse.of(response);

            writeResponse(outputStream, httpResponse.createResponse());
        } catch (final IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createRequest(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestLine requestLine = HttpRequestLine.of(bufferedReader.readLine());
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.of(readHttpRequestHeader(bufferedReader));

        final String requestBody = readHttpRequestBody(bufferedReader, httpRequestHeader);

        return HttpRequest.of(requestLine, httpRequestHeader, requestBody);
    }

    private List<String> readHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpRequest = new ArrayList<>();

        String line = bufferedReader.readLine();
        while (line != null && !line.equals("")) {
            httpRequest.add(line);
            line = bufferedReader.readLine();
        }

        return httpRequest;
    }

    private String readHttpRequestBody(final BufferedReader bufferedReader, final HttpRequestHeader httpRequestHeader)
            throws IOException {
        if (!httpRequestHeader.contains("Content-Length")) {
            return "";
        }

        final int contentLength = Integer.parseInt(httpRequestHeader.getHeader("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private ResponseEntity handleRequest(final HttpRequest httpRequest)
            throws IOException {
        final String path = httpRequest.getPath();

        if (FileHandler.isStaticFilePath(path)) {
            return FileHandler.createFileResponse(path);
        }

        final HttpFrontServlet frontServlet = new HttpFrontServlet();
        return frontServlet.service(httpRequest);
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
