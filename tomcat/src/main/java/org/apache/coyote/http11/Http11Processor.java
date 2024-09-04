package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_FOLDER_NAME = "static";

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }

            String responseBody = "Hello world!";
            MediaType mediaType = MediaType.HTML;
            String[] splitLine = firstLine.split(" ");
            String httpMethod = splitLine[0];
            String requestUri = splitLine[1];

            if ("GET".equals(httpMethod) && !"/".equals(requestUri)) {
                URL resource = getClass().getClassLoader().getResource(STATIC_FOLDER_NAME + requestUri);
                if (resource == null) {
                    return;
                }
                File file = new File(resource.getFile());
                String extension = extractExtension(file.getName());
                mediaType = MediaType.from(extension);
                responseBody = new String(Files.readAllBytes(file.toPath()));
            }
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + mediaType.getValue() + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractExtension(String uri) {
        int lastIndex = uri.lastIndexOf('.');
        if (lastIndex == -1 && lastIndex == uri.length() - 1) {
            return "";
        }
        return uri.substring(lastIndex);
    }
}
