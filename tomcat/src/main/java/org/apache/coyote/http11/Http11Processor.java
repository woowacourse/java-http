package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.catalina.engine.CatalinaServletEngine;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (bufferedReader.ready()) {
                Map<RequestLine, String> requestLineElements = extractRequestLine(bufferedReader);
                Map<String, String> headers = parseHeaders(bufferedReader);
                StringBuilder response = new StringBuilder();

                CatalinaServletEngine.processRequest(requestLineElements, headers, response);

                outputStream.write(response.toString().getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Map<RequestLine, String> extractRequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        String[] requestLineElements = line.split(" ");
        return Map.of(RequestLine.HTTP_METHOD, requestLineElements[0],
                RequestLine.REQUEST_URI, requestLineElements[1],
                RequestLine.HTTP_VERSION, requestLineElements[2]);
    }

    private static Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(line, ":");
            String key = tokenizer.nextToken().trim();
            String value = tokenizer.nextToken("").trim();
            headerMap.put(key, value);
            line = bufferedReader.readLine();
        }
        return headerMap;
    }
}
