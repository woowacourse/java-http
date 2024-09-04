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
    private static final String DEFAULT_EXTENSION = MimeType.HTML.getExtension();

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
                String fileName = getFileName(endpoint);
                String responseBody = getResponseBody(fileName);
                MimeType mimeType = MimeType.getMimeType(fileName);

                response = createResponse(HttpStatus.OK, responseBody, mimeType);
            } catch (UncheckedServletException e) {
                log.error("Error processing request for endpoint: {}", endpoint, e);

                String responseBody = getResponseBody("404.html");
                response = createResponse(HttpStatus.NOT_FOUND, responseBody, MimeType.HTML);
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

    private String getFileName(String endpoint) {
        String fileName = endpoint.substring(1);
        if (fileName.isEmpty()) {
            fileName = "hello.html";
        }
        if(isWithoutExtension(fileName)){
            fileName = String.join(".",fileName,DEFAULT_EXTENSION);
        }
        return fileName;
    }

    private boolean isWithoutExtension(String fileName) {
        return !fileName.contains(".");
    }

    private String getResponseBody(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new UncheckedServletException("Cannot find resource with name: " + fileName);
        }
        log.info("Loading resource from: {}", resource);
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private URL findResource(String fileName) {
        return getClass().getClassLoader().getResource("static/" + fileName);
    }

    private static String createResponse(HttpStatus status, String responseBody, MimeType mimeType) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.getValue() + " ",
                "Content-Type: " + mimeType.getType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
