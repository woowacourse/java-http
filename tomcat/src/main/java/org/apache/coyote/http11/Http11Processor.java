package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
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
            final List<String> headers = new ArrayList<>();

            String line;
            while (!"".equals(line = bufferedReader.readLine())) {
                headers.add(line);
            }

            int contentLength = 0;
            for (String header : headers) {
                if (header.toLowerCase().startsWith("content-length")) {
                    contentLength = Integer.parseInt(header.split(":")[1].trim());
                }
            }

            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            final String body = new String(buffer);

            if (headers.getFirst() == null) {
                return;
            }

            final var response = new RequestProcessor().process(headers, body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
