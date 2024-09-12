package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.catalina.adapter.CoyoteAdapter;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String CRLF = "\r\n";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final CoyoteAdapter adapter;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.adapter = new CoyoteAdapter();
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

            final var response = service(bufferedReader);

            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (final IOException exception) {
            log.error("[I/O ERROR] : {}", exception.getMessage());
        }
    }

    private Response service(final BufferedReader bufferedReader) throws IOException {
        final var plaintext = parseLines(bufferedReader);
        final var request = new Request(plaintext);
        return adapter.service(request);
    }

    private String parseLines(final BufferedReader bufferedReader) throws IOException {
        final var collector = new ArrayList<String>();
        var line = bufferedReader.readLine();
        var contentLength = 0;
        while (!Objects.isNull(line) && !line.isBlank()) {
            collector.add(line);
            line = bufferedReader.readLine();
            contentLength = findContentLength(line, contentLength);
        }
        collector.add("");
        collector.addAll(parseBody(bufferedReader, contentLength));

        return String.join(CRLF, collector);
    }

    private int findContentLength(final String line, final int origin) {
        if (line.startsWith("Content-Length") && origin == 0) {
            final List<String> content = List.of(line.replaceAll(" ", "").split(":"));
            return Integer.parseInt(content.getLast());
        }
        return origin;
    }

    private List<String> parseBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        if (contentLength > 0) {
            var buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return List.of(new String(buffer).split(CRLF));
        }
        return List.of();
    }
}
