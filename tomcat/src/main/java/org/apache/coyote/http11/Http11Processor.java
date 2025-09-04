package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String DEFAULT_RESOURCE_PATH = "static";

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

            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            final String firstLineOfHeader = br.readLine();
            final String requestUri = firstLineOfHeader.split(" ")[1];
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
            }

            final String contentType = getContentType(requestUri);
            final String responseBody = getResponseBody(requestUri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/%s;charset=utf-8 ".formatted(contentType),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "css";
        }
        if (requestUri.equals("js")) {
            return "js";
        }
        if (requestUri.equals("svg")) {
            return "svg+xml";
        }
        return "html";
    }

    private String getResponseBody(final String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        if (hasQueryString(requestUri)) {
            return getResponseBodyIfHasQueryString(requestUri);
        }

        return getPlainResponseBody(requestUri);
    }

    private boolean hasQueryString(final String requestUri) {
        return requestUri.contains("?");
    }

    private String getResponseBodyIfHasQueryString(final String requestUri) throws IOException {
        int index = requestUri.indexOf("?");

        final String queryString = requestUri.substring(index + 1);
        final String[] queryStrings = queryString.split("&");

        final Map<String, String> params = new HashMap<>();
        for (String param : queryStrings) {
            final String name = param.split("=")[0];
            final String value = param.split("=")[1];
            params.put(name, value);
        }
        final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(params.get("account"));
        userOrEmpty.ifPresent(user -> log.info("user: {}", user));

        final String filePath = DEFAULT_RESOURCE_PATH + requestUri.substring(0, index);

        return readResourceAsString(requestUri, filePath);
    }

    private String getPlainResponseBody(final String requestUri) throws IOException {
        final String filePath = DEFAULT_RESOURCE_PATH + requestUri;

        return readResourceAsString(requestUri, filePath);
    }

    private String readResourceAsString(final String requestUri, String filePath) throws IOException {
        if (!requestUri.contains(".")) {
            filePath += ".html";
        }

        URL resource  = getClass().getClassLoader().getResource(filePath);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource(DEFAULT_RESOURCE_PATH + "/404.html");
        }
        final File file = new File(resource.getFile());
        final Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
