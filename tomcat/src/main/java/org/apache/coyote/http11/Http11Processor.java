package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final String requestUrl = parseUrl(reader);
            log.info("request url : {}", requestUrl);
            if (reader.readLine() == null) {
                return;
            }
            final Map<String, String> header = parseHeader(reader);
            final String responseBody = getResourceFromUrl(requestUrl);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + parseContentType(header) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUrl(final BufferedReader reader) throws IOException {
        return reader.readLine().split(" ")[1];
    }

    private Map<String, String> parseHeader(final BufferedReader reader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line = reader.readLine();
        while (!"".equals(line)) {
            final String[] splitLine = line.split(": ", 2);
            header.put(splitLine[0], splitLine[1]);
            line = reader.readLine();
        }
        return header;
    }

    private String parseContentType(final Map<String, String> header) {
        return header
                .getOrDefault("Accept", "text/html")
                .split(",")[0];
    }

    private String getResourceFromUrl(final String requestUrl) throws URISyntaxException, IOException {
        if (!requestUrl.equals("/")) {
            final URI uri = getClass()
                    .getClassLoader()
                    .getResource("static" + requestUrl)
                    .toURI();
            return new String(Files.readAllBytes(Path.of(uri)));
        }
        return "Hello world!";
    }
}
