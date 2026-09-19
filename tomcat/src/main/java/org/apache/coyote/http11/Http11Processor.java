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
import java.util.Optional;

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
            Path filePath = getFilePath(uriPath);

            if (uriPath.equals("/login")) {
                final String query = uri.getQuery();
                if (query != null) {
                    final Map<String, String> params = extractQueryParams(query);
                    filePath = login(params);
                }
            }

            final String contentType = getContentType(requestTarget);
            final String responseBody = getResponseBody(filePath);
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
            return resolveResourcePath("static/login.html");
        }
        else {
            return resolveResourcePath("static" + uriPath);
        }
    }

    private Map<String, String> extractQueryParams(final String query) {
        final String[] queryParams = query.split(QUERY_PARAM_DELIMITER);

        final Map<String, String> params = new HashMap<>();

        for (String queryParam : queryParams) {
            String[] pair = queryParam.split(QUERY_PARAM_VALUE_DELIMITER, 2);
            String key = pair[0];
            String value = pair.length == 2 ? pair[1] : "";

            params.put(key, value);
        }
        return params;
    }

    private Path login(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            log.error("login error");
            return resolveResourcePath("static/401.html");
        }

        final User userByAccount = user.get();
        if (!userByAccount.checkPassword(password)) {
            log.error("login error");
            return resolveResourcePath("static/401.html");
        }

        log.info("user : {}", userByAccount);
        return resolveResourcePath("static/index.html");
    }

    private String getResponseBody(final Path filePath) throws IOException {
        if (filePath.equals(Path.of("/"))) {
            return "Hello world!";
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

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null)
            return Path.of("/");
        return Path.of(url.getPath());
    }
}
