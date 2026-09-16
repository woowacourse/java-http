package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_CONTENT_TYPE = "text/html";

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
            Map<String, String> requestInformations = parseRequestFromInputStream(inputStream);

            var responseBody = consistProperBodyContents(requestInformations);

            final var response = consistResponseWithBodyAndHeader(requestInformations, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String consistResponseWithBodyAndHeader(Map<String, String> requestInformations, String responseBody) {
        String contentType = resolveContentType(requestInformations.get("Accept"));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

    }

    private String resolveContentType(String accept) {
        if (accept == null || accept.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }

        String preferred = accept.split(",")[0]
                .split(";")[0]
                .trim();

        if (preferred.isEmpty() || preferred.equals("*/*")) {
            return DEFAULT_CONTENT_TYPE;
        }

        return preferred;
    }

    private String consistProperBodyContents(Map<String, String> requestInformations)
            throws URISyntaxException, IOException {
        StringBuilder sb = new StringBuilder();
        String requestEndPoint = requestInformations.get("endpoint");

        if (!requestEndPoint.equals("/")) {
            String fileName = requestEndPoint.replaceFirst("/", "");

            URL resource = getClass().getClassLoader().getResource("static/" + fileName);
            if (resource == null) {
                return "";
            }
            Path path = Path.of(resource.toURI());

            return sb.append(Files.readString(path)).toString();
        }

        return "Hello world!";
    }

    private Map<String, String> parseRequestFromInputStream(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            Map<String, String> request = new HashMap<>();

            String requestLine = reader.readLine();
            request.put("endpoint", requestLine.split(" ")[1]);

            reader.lines()
                    .takeWhile(line -> !line.isBlank())
                    .map(line -> line.split(":", 2))
                    .forEach(attribute ->
                            request.put(attribute[0].trim(), attribute[1].trim())
                    );

            return request;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
