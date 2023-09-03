package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String REQUEST_START_LINE_DELIMITER = " ";
    private static final int REQUEST_START_LINE_ELEMENT_SIZE = 3;
    private static final int REQUEST_TARGET_INDEX = 1;

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
            final InputStream inputStream = connection.getInputStream();
            final InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final OutputStream outputStream = connection.getOutputStream()
        ) {
            final String response = getResponse(getTargetUrl(bufferedReader.readLine()));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final String targetUrl) throws IOException {
        final ResponseBodyFinder responseBodyFinder = new ResponseBodyFinder();

        if (responseBodyFinder.isBodyExist(targetUrl)) {
            final byte[] responseBody = responseBodyFinder.getBody(targetUrl);

            return String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody));
        }
        return String.join(System.lineSeparator(), "HTTP/1.1 200 OK ");
    }

    private String getTargetUrl(final String startLine) {
        final String[] startLineElements = startLine.split(REQUEST_START_LINE_DELIMITER);
        if (startLineElements.length != REQUEST_START_LINE_ELEMENT_SIZE) {
            throw new IllegalArgumentException("Invalid HTTP request start line: " + startLine);
        }
        return startLineElements[REQUEST_TARGET_INDEX];
    }
}
