package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.component.exceptionhandler.ExceptionHandlerMapper;
import org.apache.coyote.http11.component.handler.HandlerMapper;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.response.HttpResponse;
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
             final var outputStream = connection.getOutputStream();
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader)) {

            final var plaintext = readLines(bufferedReader);
            final HttpResponse response = convert(plaintext);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();

        } catch (final IOException exception) {
            log.error("[ERROR] : {}", exception.getMessage());
        }
    }

    private String readLines(final BufferedReader bufferedReader) throws IOException {
        final var collector = new ArrayList<String>();
        var line = bufferedReader.readLine();
        while (!Objects.isNull(line) && !line.isBlank()) {
            collector.add(line);
            line = bufferedReader.readLine();
        }
        return String.join("\r\n", collector);
    }

    private HttpResponse convert(final String plaintext) {
        try {
            final var request = new HttpRequest(plaintext);
            final var handler = HandlerMapper.get(request.getPath());
            return handler.handle(request);
        } catch (final Exception exception) {
            final var handler = ExceptionHandlerMapper.get(exception.getClass());
            return handler.handle();
        }
    }
}
