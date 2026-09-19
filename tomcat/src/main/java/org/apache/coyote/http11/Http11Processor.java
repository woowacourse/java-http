package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            String requestLine = bufferedReader.readLine();
            final String requestTarget = requestLine.split(" ")[1];

            final URI uri = URI.create(requestTarget);
            log.info("request uri: {}", uri);

            final String uriPath = uri.getPath();
            final Path filePath = getFilePath(uriPath);

            if (uriPath.equals("/login")) {
                final String query = uri.getQuery();
                if (query != null) {
                    final Map<String, String> params = extractQueryParams(query);
                    login(params);
                }
            }

            final String responseBody = getResponseBody(uriPath, filePath);
            final String contentType = getContentType(requestTarget);
            final String httpStatus = "200 OK";

            final var response = createResponse(contentType, responseBody, httpStatus);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Path getFilePath(final String uriPath) {
        if (uriPath.equals("/")) {
            return Path.of("/");
        }
        if (uriPath.equals("/login")) {
            final URL url = getClass().getClassLoader().getResource("static/login.html");
            if (url == null)
                return Path.of("/");
            return Path.of(url.getPath());
        }
        else {
            final URL url = getClass().getClassLoader().getResource("static" + uriPath);
            if (url == null)
                return Path.of("/");
            return Path.of(url.getPath());
        }
    }

    private Map<String, String> extractQueryParams(final String query) {
        final String[] queryParams = query.split(QUERY_PARAM_DELIMITER);

        final Map<String, String> params = new HashMap<>();

        for (String queryParam : queryParams) {
            final String key = queryParam.split(QUERY_PARAM_VALUE_DELIMITER)[0];
            final String value = queryParam.split(QUERY_PARAM_VALUE_DELIMITER)[1];

            params.put(key, value);
        }
        return params;
    }

    private void login(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        final User userByAccount = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!userByAccount.checkPassword(password)) {
            log.error("login error");
            throw new IllegalArgumentException();
        }

        log.info("user : {}", userByAccount);
    }

    private String getResponseBody(final String uriPath, final Path filePath) throws IOException {
        if (uriPath.equals("/")) {
            return  "Hello world!";
        }

        return Files.readString(filePath);
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private String createResponse(final String contentType, final String responseBody, final String httpStatus) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
