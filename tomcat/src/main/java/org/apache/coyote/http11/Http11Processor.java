package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.error.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestDispatcher requestDispatcher;

    public Http11Processor(final Socket connection, final RequestDispatcher requestDispatcher) {
        this.connection = connection;
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
            final var outputStream = connection.getOutputStream()) {
            dispatchRequest(bufferedReader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dispatchRequest(final BufferedReader bufferedReader,
        final OutputStream outputStream) throws IOException {
        final HttpResponse response = HttpResponse.init();

        try {
            final HttpRequest request = readRequest(bufferedReader);

            requestDispatcher.dispatch(request, response);
            writeResponse(outputStream, response.getMessage());
        } catch (HttpException e) {
            response.addStatusLine(StatusLine.http11(e.status()));
            response.addHeader("Content-Length", "0");
            writeResponse(outputStream, response.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResponse(final OutputStream outputStream, final String message)
        throws IOException {
        outputStream.write(message.getBytes());
        outputStream.flush();
    }

    private HttpRequest readRequest(final BufferedReader bufferedReader) throws IOException {
        final String rawRequestLine = bufferedReader.readLine();
        final RequestLine requestLine = RequestLine.from(rawRequestLine);
        final HttpHeaders headers = HttpHeaders.from(readRequestHeaderLines(bufferedReader));
        final String requestBody =
            readRequestBody(bufferedReader, headers.valueOf("Content-Length"));

        return new HttpRequest(
            requestLine,
            headers,
            requestBody);
    }

    private List<String> readRequestHeaderLines(final BufferedReader bufferedReader)
        throws IOException {
        final List<String> requestHeaderLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            requestHeaderLines.add(line);
        }

        return requestHeaderLines;
    }

    private String readRequestBody(final BufferedReader bufferedReader,
        final String rawContentLength)
        throws IOException {
        if (rawContentLength == null || rawContentLength.isEmpty()) {
            return "";
        }
        final int contentLength = Integer.parseInt(rawContentLength.trim());
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer).trim();
    }
}
