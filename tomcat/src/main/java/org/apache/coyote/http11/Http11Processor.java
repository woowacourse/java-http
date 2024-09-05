package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

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
            if (bufferedReader.ready()) {
                Map<RequestLine, String> requestLineElements = extractRequestLine(bufferedReader);
                HttpResponse httpResponse = HttpResponse.from("HTTP/1.1");

                CatalinaServletEngine.processRequest(requestLineElements, httpResponse);

                outputStream.write(httpResponse.buildResponse().getBytes());
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
}
