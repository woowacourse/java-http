package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_PATH = "static/";
    private static final String ROOT_PATH = "/";

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
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = readHttpRequest(bufferedReader);

            final String responseBody = findResponseBody(httpRequest);
            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader bufferedReader) throws IOException {
        final var requestLines = bufferedReader.readLine();

        final var requestStartLine = requestLines.split(" ");
        final var requestMethod = requestStartLine[0];
        final var requestEndPoint = requestStartLine[1];
        final var requestVersion = requestStartLine[2]
                .replace("/", "_")
                .replace(".", "_");

        // TODO: GET, OPTION 이 아닌 경우 BODYPUBLISH
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + requestEndPoint))
                .version(Version.valueOf(requestVersion))
                .method(requestMethod, BodyPublishers.noBody())
                .build();
    }

    private String findResponseBody(HttpRequest httpRequest) throws IOException {
        String endPoint = httpRequest.uri().getPath();
        if (endPoint.equals(ROOT_PATH)) {
            return "Hello world!";
        }

        final String fileName = endPoint.replace(ROOT_PATH, STATIC_RESOURCE_PATH);
        final URL resourceURL = getClass().getClassLoader().getResource(fileName);

        return Files.readString(Path.of(resourceURL.getPath()));
    }
}
