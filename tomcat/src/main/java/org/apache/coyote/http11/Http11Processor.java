package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import com.techcourse.model.LoginParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            final var request = getRequest(reader);

            String[] requestLineParts = getRequestLineParts(request);

            String method = requestLineParts[0];
            String requestPath = requestLineParts[1];

            if (isGetMethod(method)) {
                if (requestPath.equals("/")) {
                    final var response = getResponse();

                    sendResponse(outputStream, response);
                    return;
                }

                if (requestPath.startsWith("/login")) {
                    authenticateUserFromRequestPath(requestPath);

                    serveStaticFile(requestPath, outputStream);
                    return;
                }

                serveStaticFile(requestPath, outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serveStaticFile(String requestPath, OutputStream outputStream) throws IOException {
        final var path = getPath(requestPath);
        final var responseBody = Files.readString(path);
        final var response = getResponse(responseBody, path);

        sendResponse(outputStream, response);
    }

    private Path getPath(String requestPath) {
        if (requestPath.startsWith("/login")) {
            return Path.of(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResource("static/login.html")).getPath()
            );
        }

        var resource = getClass().getClassLoader().getResource("static/" + requestPath);
        return Path.of(Objects.requireNonNullElseGet(
                resource, () -> Objects.requireNonNull(
                        getClass().getClassLoader().getResource("static/404.html")
                )
        ).getPath());
    }

    private String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
    }

    private String getResponse(String responseBody, Path path) throws IOException {
        if (path.toAbsolutePath().endsWith("404.html")) {
            responseBody = "404 NOT FOUND";
        }
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getRequest(BufferedReader reader) throws IOException {
        final var request = reader.readLine();
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException("[ERROR] request is empty");
        }

        return request.trim();
    }

    private String[] getRequestLineParts(String request) {
        String[] requestLineParts = request.split(" ");
        if (requestLineParts.length < 3) {
            throw new IllegalArgumentException("[ERROR] invalid request: " + request);
        }

        return requestLineParts;
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void authenticateUserFromRequestPath(String requestPath) {
        int index = requestPath.indexOf("?");
        String queryString = requestPath.substring(index + 1);

        Map<LoginParam, String> accountAndPassword = queryParser(queryString);

        String account = accountAndPassword.get(LoginParam.ACCOUNT);
        String password = accountAndPassword.get(LoginParam.PASSWORD);

        User user = InMemoryUserRepository.findByAccount(account, password)
                .orElseThrow(() ->  new IllegalArgumentException("[ERROR] no such user"));

        user.validatePasswordAndLog(password);
        log.info(user.toString());
    }

    private Map<LoginParam, String> queryParser(String queryString) {
        String[] accountAndPassword = queryString.split("&");
        String[] accountInfo = accountAndPassword[0].split("=");
        String[] passwordInfo = accountAndPassword[1].split("=");

        String account = accountInfo[1];
        String password = passwordInfo[1];

        return Map.of(
                LoginParam.ACCOUNT, account,
                LoginParam.PASSWORD, password
        );
    }

    private boolean isGetMethod(String method) {
        return method.equals("GET");
    }
}
