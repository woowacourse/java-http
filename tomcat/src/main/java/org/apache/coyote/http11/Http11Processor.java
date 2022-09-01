package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_REQUEST_BODY = "Hello world!";
    private static final String STATIC_FILE_PATH = "static";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final Socket connection;
    private final Map<String, String> headers = new HashMap<>();

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            List<String> request = getRequest(bufferedReader);
            analyzeHeaders(request);

            final var requestURI = getRequestURI(request);
            final var responseBody = getResponseBody(requestURI);
            final var response = makeResponse(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

    private String getRequestURI(final List<String> request) {
        return request.get(0).split(" ")[1];
    }

    private String getResponseBody(final String requestURI) throws IOException {
        if (!requestURI.equals("/")) {
            final URL url = getClass().getClassLoader().getResource(STATIC_FILE_PATH + requestURI);
            final Path path = new File(url.getFile()).toPath();
            return new String(Files.readAllBytes(path));
        }
        return DEFAULT_REQUEST_BODY;
    }

    private void analyzeHeaders(final List<String> request) {
        for (String header : request.subList(1, request.size())) {
            String[] splitHeader = header.split(": ", 2);
            headers.put(splitHeader[0], splitHeader[1]);
        }
    }

    private String makeResponse(final String responseBody) {
        String contentType = headers.getOrDefault("Accept", DEFAULT_CONTENT_TYPE).split(",")[0];

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
