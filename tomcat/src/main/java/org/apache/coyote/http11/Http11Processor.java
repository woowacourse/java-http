package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.presentation.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            final RequestLine requestLine = new RequestLine(line);

            final List<String> headers = new ArrayList<>();
            while (!"".equals(line = bufferedReader.readLine())) {
                headers.add(line);
            }

            final HttpRequest requestForHeader = HttpRequest.builder().requestLine(requestLine).headers(headers)
                    .build();
            final int contentLength = requestForHeader.getContentLength();

            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            final String body = new String(buffer);

            final HttpRequest request = HttpRequest.builder()
                    .requestLine(requestLine)
                    .headers(headers)
                    .params(body)
                    .build();

            final var response = new RequestProcessor().process(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
