package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            if (!validateHeaders(bufferedReader)) {
                return;
            }

            final String[] requestComponents = requestLine.split(" ");
            final String requestUri = requestComponents[1];

            final int queryIndex = requestUri.indexOf('?');
            String path;
            final String queryString;
            if (queryIndex >= 0) {
                path = requestUri.substring(0, queryIndex);
                queryString = requestUri.substring(queryIndex + 1);
            } else {
                path = requestUri;
                queryString = "";
            }

            String code = "200";
            String status = "OK";
            if ("/login".equals(path)) {
                if (!queryString.isBlank()) {
                    final Map<String, String> parameters = parseQueryString(queryString);
                    if (login(parameters)) {
                        path = "/index";
                        code = "302";
                        status = "FOUND";
                    } else {
                        path = "/401";
                        code = "401";
                        status = "UNAUTHORIZED";
                    }
                }
                path += ".html";
            }

            final var response = makeResponse(path, requestUri, code, status);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(
            String path,
            String requestUri,
            String code,
            String status
    ) throws IOException {
        final String responseBody = getResponseBody(path);
        final String contentType = resolveContentType(requestUri);

        return String.join("\r\n",
                "HTTP/1.1 " + code + " " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String getResponseBody(String path) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!";
        }
        final String resourceName = "static" + path;
        final String fileName = Objects.requireNonNull(
                getClass().getClassLoader().getResource(resourceName),
                "리소스를 찾을 수 없음: " + resourceName
        ).getPath();

        return Files.readString(Path.of(fileName));
    }

    private String resolveContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        if (queryString.isBlank()) {
            return parameters;
        }

        for (String pair : queryString.split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length != 2) {
                continue;
            }
            parameters.put(nameAndValue[0], nameAndValue[1]);
        }
        return parameters;
    }

    private boolean login(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (loginUser.isEmpty()) {
            return false;
        }

        log.info("로그인 성공: {}", loginUser);
        return true;
    }

    private static boolean validateHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                return false;
            }
            line = bufferedReader.readLine();
        }
        return true;
    }
}
