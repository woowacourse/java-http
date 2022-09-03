package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequestMapper;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestMapper requestMapper;

    public Http11Processor(final Socket connection, final HttpRequestMapper requestMapper) {
        this.connection = connection;
        this.requestMapper = requestMapper;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var reader = new BufferedReader(streamReader);
             final var outputStream = connection.getOutputStream()) {

            String response = requestMapper.handle(toRequest(reader));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest toRequest(BufferedReader reader) throws IOException {
        List<String> request = new ArrayList<>();
        String line;
        while ((line = reader.readLine()).length() > 0) {
            request.add(line);
        }
        return HttpRequest.of(request);
    }
}
