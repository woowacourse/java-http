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
            log.info("request uri: {}", requestTarget);

            final URI uri = URI.create(requestTarget);

            String responseBody;
            if (requestTarget.equals("/")) {
                responseBody = "Hello world!";
            }
            else if (requestTarget.startsWith("/login")) {
                URL url = getClass().getClassLoader().getResource("static/login.html");
                if (url == null)
                    return;
                final Path path = Path.of(url.getPath());

                final Map<String, String> params = extractQueryParams(uri);

                login(params);

                responseBody = Files.readString(path);
            }
            else {
                URL url = getClass().getClassLoader().getResource("static/" + uri.getPath());
                if (url == null)
                    return;
                final Path path = Path.of(url.getPath());
                responseBody = Files.readString(path);
            }

            final String contentType = getContentType(requestTarget);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> extractQueryParams(final URI uri) {
        final String[] queryParams = uri.getQuery().split(QUERY_PARAM_DELIMITER);

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

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}
