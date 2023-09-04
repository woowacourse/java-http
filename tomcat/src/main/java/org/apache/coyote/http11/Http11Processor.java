package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NO_MATCH_USER_MESSAGE = "가입하지 않은 유저입니다.";

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
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);

            final String uri = readRequestHeader(bufferedReader);
            final String response = handleRequest(Objects.requireNonNull(uri));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleRequest(final String uri) throws IOException {
        final String path = uri.split("\\?")[0];

        if (path.equals("/")) {
            return getResponseMessage("Hello world!", "text/html;");
        }
        if (path.endsWith(".css")) {
            final String responseBody = findResponseBody(path);
            return getResponseMessage(responseBody, "text/css;");
        }
        if (path.equals("/login")) {
            final String responseBody = findResponseBody(path + ".html");

            final Map<String, String> queryStrings = getQueryStrings(uri);
            final User user = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                    .orElseThrow(() -> new NoSuchElementException(NO_MATCH_USER_MESSAGE));
            log.info("User: {}", user);
            return getResponseMessage(responseBody, "text/html;");
        }
        final String responseBody = findResponseBody(path);
        return getResponseMessage(responseBody, "text/html;");
    }

    private Map<String, String> getQueryStrings(final String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }
        final Map<String, String> queryStrings = new HashMap<>();
        final String queryString = uri.split("\\?")[1];

        final String[] splitQueryStrings = queryString.split("&");
        for (String value : splitQueryStrings) {
            final String[] splitValue = value.split("=");
            queryStrings.put(splitValue[0], splitValue[1]);
        }
        return queryStrings;
    }

    private String getResponseMessage(final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String findResponseBody(final String uri) throws IOException {
        final URL fileUrl = getClass().getClassLoader().getResource("./static" + uri);
        final String filePath = Objects.requireNonNull(fileUrl).getPath();
        return Files.readString(new File(filePath).toPath());
    }

    private String readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        final String uri = line.split(" ")[1];

        while (!"".equals(line)) {
            line = bufferedReader.readLine();
        }
        return uri;
    }
}
