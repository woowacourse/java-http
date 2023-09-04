package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            log.info("start process");
            final HttpRequest request = HttpRequest.from(bufferedReader);

            final String uriPath = request.getUriPath();
            log.info(uriPath);

            final String httpStatusOk = "200 OK";
            final String contentType = "text/html;charset=utf-8";
            final String responseBody = readStaticFile(uriPath);
            final HttpResponse response = new HttpResponse(httpStatusOk, contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String readStaticFile(final String fileName) throws URISyntaxException {
        final String filePath = "static/" + fileName;
        final URL res = getClass().getClassLoader().getResource(filePath);

        try {
            return new String(Files.readAllBytes(new File(res.getFile()).toPath()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }

}
