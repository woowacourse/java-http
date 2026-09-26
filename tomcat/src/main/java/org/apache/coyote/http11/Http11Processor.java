package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
        try (
                final var bufferedReader = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );
                final var outputStream = connection.getOutputStream()) {

            String requestLine = bufferedReader.readLine();
            String[] splitRequestLine = requestLine.split(" ");

            String method = splitRequestLine[0];
            String requestTarget = splitRequestLine[1];
            String protocol = splitRequestLine[2];

            if (method.equals("GET")) {
                handleGetRequest(outputStream, requestTarget);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleGetRequest(OutputStream outputStream, String requestTarget) throws IOException {
        String contentType;
        final byte[] responseBody;

        // root 처리
        if (requestTarget.equals("/")) {
            writeResponse(outputStream,
                    HttpStatus.OK,
                    "text/html;charset=utf-8 ",
                    "Hello world!".getBytes(StandardCharsets.UTF_8));

            return;
        }

        ParsedTarget parsedTarget = parseRequestTarget(requestTarget);
        String resourceName = parsedTarget.path();
        HttpStatus status = HttpStatus.OK;
        Map<String, String> queryParameters = parsedTarget.queryParameters();

        if (resourceName.equals("/login")) {
            if (queryParameters.get("account") == null || queryParameters.get("password") == null) {
                status = HttpStatus.OK;
                resourceName = "login.html";
            }
            else if (isLoginSuccessful(queryParameters)) {
                status = HttpStatus.OK;
                resourceName = "index.html";
            } else {
                status = HttpStatus.UNAUTHORIZED;
                resourceName = "401.html";
            }
        }

        contentType = resolveContentType(resourceName);
        responseBody = readResponseBody(resourceName);

        writeResponse(outputStream, status, contentType, responseBody);
    }

    private ParsedTarget parseRequestTarget(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf("?");

        if (queryStartIndex < 0) {
            return new ParsedTarget(
                    requestTarget,
                    new HashMap<>()
            );
        }

        String path = requestTarget.substring(0, queryStartIndex);
        String queryString = requestTarget.substring(queryStartIndex + 1);

        return new ParsedTarget(path, parseQueryParameters(queryString));
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();

        if (queryString.isEmpty()) {
            return queryParameters;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                queryParameters.put(keyValue[0], keyValue[1]);
            }
        }

        return queryParameters;
    }

    private static boolean isLoginSuccessful(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private static String resolveContentType(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }

        if (resourceName.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }

        return "application/octet-stream";
    }

    private byte[] readResponseBody(String resourceName) throws IOException {
        final byte[] responseBody;

        final URL resource = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource("static/" + resourceName),
                "해당 리소스를 찾을 수 없습니다."
        );
        final Path path = new File(resource.getFile()).toPath();

        responseBody = Files.readAllBytes(path);
        return responseBody;
    }

    private void writeResponse(OutputStream outputStream,
                                      HttpStatus status,
                                      String contentType,
                                      byte[] responseBody) throws  IOException{
        final var header = String.join("\r\n",
                "HTTP/1.1 " + status.getCode() + " " + status.getReasonPhrase() + " ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.length + " ",
                "",
                "");

        outputStream.write(header.getBytes());
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
