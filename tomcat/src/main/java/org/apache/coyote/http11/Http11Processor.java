package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

            final HttpResponse response = handleHttp(plaintext);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (final IOException exception) {
            log.error("[I/O ERROR] : {}", exception.getMessage());
        }
    }

    private HttpResponse handleHttp(final String plaintext) {
        try {
            final var request = new HttpRequest(plaintext);
            final var handler = HandlerMapper.get(request.getPath());
            return handler.handle(request);
        } catch (final Exception exception) {
            log.warn("[KNOWN ERROR] : {}", exception.getMessage());
            final var handler = ExceptionHandlerMapper.get(exception.getClass());
            return handler.handle();
        }
    }

    private String readLines(final BufferedReader bufferedReader) throws IOException {
        final var collector = new ArrayList<String>();
        var line = bufferedReader.readLine();
        var contentLength = 0;
        while (!Objects.isNull(line) && !line.isBlank()) {
            collector.add(line);
            line = bufferedReader.readLine();
            if (line.startsWith("Content-Length")) {
                final List<String> content = List.of(line.replaceAll(" ", "").split(":"));
                contentLength = Integer.parseInt(content.getLast());
            }
        }
        collector.add("");
        collector.addAll(parseBody(bufferedReader, contentLength));

        return String.join("\r\n", collector);
    }

    private List<String> parseBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        if (contentLength > 0) {
            var buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return List.of(new String(buffer).split("\r\n"));
        }
        return List.of();
    }

}
