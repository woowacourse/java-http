package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
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

            final Map<String, String> httpRequestMessage = new HashMap<>();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final String startLine = bufferedReader.readLine();
            addStartLine(httpRequestMessage, startLine);
            addHeader(httpRequestMessage, bufferedReader);
            addBody(httpRequestMessage, bufferedReader);

            final String responseBody = getResponseBody(httpRequestMessage.get("path"));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalStateException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addStartLine(final Map<String, String> httpRequestMessage, final String startLine) {
        final String[] startLines = splitStartLine(startLine);
        if (startLines.length != 3) {
            throw new IllegalStateException("올바르지 않은 HTTP 스타트 라인입니다.");
        }
        httpRequestMessage.put("method", startLines[0]);
        httpRequestMessage.put("path", startLines[1]);
        httpRequestMessage.put("version", startLines[2]);
    }

    private String[] splitStartLine(final String startLine) {
        if (startLine == null || startLine.isEmpty() || startLine.isBlank()) {
            throw new IllegalStateException("HTTP 스타트 라인이 존재하지 않습니다.");
        }
        return startLine.split(" ");
    }

    private void addHeader(final Map<String, String> httpRequestMessage, final BufferedReader bufferedReader)
            throws IOException {
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            final String[] headers = splitHeader(line);
            if (headers.length != 2) {
                throw new IllegalStateException("올바르지 않은 HTTP 헤더 정보입니다.");
            }
            httpRequestMessage.put(headers[0], headers[1]);
        }
    }

    private String[] splitHeader(final String header) {
        if (header == null || header.isEmpty() || header.isBlank()) {
            throw new IllegalStateException("HTTP 헤더 정보가 존재하지 않습니다.");
        }
        return header.split(": ");
    }

    private void addBody(final Map<String, String> httpRequestMessage, final BufferedReader bufferedReader)
            throws IOException {
        if (httpRequestMessage.containsKey("Content-Length")) {
            final int contentLength = Integer.parseInt(httpRequestMessage.get("Content-Length"));
            final char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            httpRequestMessage.put("body", new String(body));
        }
    }

    private String getResponseBody(final String uriPath) throws IOException, URISyntaxException {
        if (uriPath != null && uriPath.contains("html")) {
            final Path path =
                    Paths.get(getClass().getClassLoader().getResource("static" + uriPath).toURI());
            return Files.readAllLines(path).stream()
                    .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
        }
        return "Hello world!";
    }
}
