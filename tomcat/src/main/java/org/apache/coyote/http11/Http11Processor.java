package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int URL_INDEX = 1;
    private static final String DEFAULT_URL = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private static final String HEADER_DELIMITER = ":";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequestLine httpRequestLine = HttpRequestLine.from(reader.readLine());
            final HttpHeaders httpHeaders = new HttpHeaders(parseRequestHeaders(reader));
            final HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpHeaders);

            printLoginUser();

            final String responseBody = getResponseBody(request.get("path"));
            final String response = createResponse(request.get("contentType"), responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseRequestHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            final String[] header = line.split(HEADER_DELIMITER);
            final String name = header[0].trim();
            final String value = header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private void printLoginUser(final String path, final String allOfQueryParam) {
        if (path.contains("login")) {
            final User user = InMemoryUserRepository.findByAccount(params.get("account"))
                    .orElseThrow(UserNotFoundException::new);
            if (user.checkPassword(params.get("password"))) {
                log.info(String.format("user : %s", user));
            }
        }
    }

    private String getResponseBody(final String path) throws IOException {
        if (path.equals(DEFAULT_URL)) {
            return DEFAULT_RESPONSE_BODY;
        }
        return readFile(path);
    }

    private String readFile(final String path) throws IOException {
        final String filePath = String.format("static%s", path);
        final URL resource = this.getClass().getClassLoader().getResource(filePath);
        return Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));
    }

    private String createResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
