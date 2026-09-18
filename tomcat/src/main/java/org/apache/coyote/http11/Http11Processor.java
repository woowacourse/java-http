package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
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
    private static final String ROOT_PATH = "/";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";

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
        String requestEndPoint = requestInformations.get("endpoint");

        int queryStringIndex = requestEndPoint.indexOf('?');
        String path = requestEndPoint;

        if (queryStringIndex != -1) {
            path = requestEndPoint.substring(0, queryStringIndex);
            Map<String, String> queryParams = parseQueryString(requestEndPoint.substring(queryStringIndex + 1));

            if (path.equals("/login")) {
                logFoundUser(queryParams);
            }
        }

        if (path.equals(ROOT_PATH)) {
            return ROOT_RESPONSE_BODY;
        }

        if (path.equals("/login")) {
            path = "/login" + ".html";
        }

        return readStaticResource(path);
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();

        for (String pair : queryString.split("&")) {
            String[] keyAndValue = pair.split("=", 2);
            if (keyAndValue.length == 2) {
                queryParams.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return queryParams;
    }

    private void logFoundUser(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("{}", user));
    }

    private String readStaticResource(String path) throws URISyntaxException, IOException {
        String fileName = path.replaceFirst(ROOT_PATH, "");

        URL resource = getClass().getClassLoader().getResource("static/" + fileName);
        if (resource == null) {
            return "";
        }

        return Files.readString(Path.of(resource.toURI()));
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
