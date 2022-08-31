package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            Map<String, String> requestHeaders = getRequestHeaders(inputStream);

            final var responseBody = getResponseBody(requestHeaders.get("REQUEST URI"));

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getRequestHeaders(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = bufferedReader.readLine();
        Map<String, String> requestHeaders = new HashMap<>();

        try {
            while (!"".equals(line)) {
                if (Objects.isNull(line)) {
                    return Map.of();
                }
                putHeader(requestHeaders, line);
                line = bufferedReader.readLine();
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("invalid HTTP request received", e.getCause());
        }
        return requestHeaders;
    }

    private void putHeader(Map<String, String> requestHeaders, String requestLine) {
        if (!requestHeaders.isEmpty()) {
            System.out.println(requestLine);
            String[] HeaderAndValue = requestLine.split(": ");
            requestHeaders.put(HeaderAndValue[0], HeaderAndValue[1]);
            return;
        }
        String[] startLine = requestLine.split(" ");
        requestHeaders.put("HTTP METHOD", startLine[0]);
        requestHeaders.put("REQUEST URI", startLine[1]);
        requestHeaders.put("HTTP VERSION", startLine[2]);
    }

    private String getResponseBody(String requestURI) throws IOException {
        if (requestURI.equals("/")) {
            return "Hello world!";
        }

        Path path = FileSystems.getDefault().getPath("src/main/resources/static", requestURI);

        return Files.readAllLines(path)
            .stream()
            .map(string -> string + "\n")
            .collect(Collectors.joining());
    }
}
