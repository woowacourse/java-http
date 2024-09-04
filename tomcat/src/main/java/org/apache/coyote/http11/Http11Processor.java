package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int ENDPOINT_POSITION = 1;

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
        try (
                final var inputStream = connection.getInputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final var outputStream = connection.getOutputStream()
        ) {
            StringBuilder request = getRequest(bufferedReader);

            String endpoint = extractEndpoint(request.toString());
            log.info("Requested endpoint: {}", endpoint);

            String response;
            try {
                String fileName = endpoint.substring(1);
                String responseBody = getResponseBody(fileName);

                response = createResponse(HttpStatus.OK, responseBody);
            } catch (UncheckedServletException e) {
                log.error("Error processing request for endpoint: {}", endpoint, e);

                String responseBody = getResponseBody("404.html");
                response = createResponse(HttpStatus.NOT_FOUND, responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static StringBuilder getRequest(BufferedReader bufferedReader) throws IOException {
        StringBuilder request = new StringBuilder();
        String line;
        while (Objects.nonNull(line = bufferedReader.readLine()) && !line.isEmpty()) {
            request.append(line).append(System.lineSeparator());
        }
        return request;
    }

    private String extractEndpoint(String request) {
        return request.split(" ")[ENDPOINT_POSITION];
    }

    private String getResponseBody(String fileName) throws IOException {
        if (fileName.isEmpty()) {
            return "Hello world!";
        }
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new UncheckedServletException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private URL findResource(String fileName) {
        return getClass().getClassLoader().getResource("static/" + fileName);
    }

    private static String createResponse(HttpStatus status, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.getValue() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
