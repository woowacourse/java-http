package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

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

            final StringBuilder requestBuilder = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
            }

            String request = requestBuilder.toString();

            String[] requestLines = request.split("\\s+");

            if (requestLines.length < 2) {
                System.out.println("request: "+request);
                throw new UncheckedServletException(new Exception("예외"));
            }
            String resourcePath = requestLines[1];
            System.out.println();

            final URL fileUrl = this.getClass().getClassLoader().getResource("static"+resourcePath);
            final byte[] responseBodyBytes = Files.readAllBytes(Paths.get(fileUrl.getPath()));

            String contentType = ContentType.from(resourcePath).getContentType();

            HttpResponse httpResponse = new HttpResponse(StatusCode.OK,request);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBodyBytes.length + " ",
                    "",
                    new String(responseBodyBytes, UTF_8));
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

}
