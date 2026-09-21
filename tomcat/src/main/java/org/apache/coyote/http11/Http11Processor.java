package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String RESOURCE_FILE_PREFIX = "static/";
    private static final String WHITESPACE_REGEX = " ";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String QUESTION_MARK = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";
    public static final String REDIRECTION_FOUND_CODE = "302";
    public static final String UNAUTHORIZED_CODE = "401";
    public static final String SUCCESS_CODE = "200";
    public static final String SUCCESS_STATUS_RESPONSE = "OK";
    public static final String ERROR_STATUS_RESPONSE = "ERROR";
    public static final String INDEX = "index";

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

            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestLine = bufferedReader.readLine();

            String[] parts = requestLine.split(WHITESPACE_REGEX);
            String part = parts[1];
            if (isRootRequest(part, outputStream)) {
                return;
            }

            String requestUri = part.substring(1);
            String statusCode = SUCCESS_CODE;

            URL resource = getClass().getClassLoader()
                .getResource(RESOURCE_FILE_PREFIX + requestUri);

            if (requestUri.equals("login")) {
                resource = getClass().getClassLoader()
                    .getResource(RESOURCE_FILE_PREFIX + requestUri + HTML_EXTENSION);
            }

            if (requestUri.contains(QUESTION_MARK)) {
                int index = requestUri.indexOf(QUESTION_MARK);
                part = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);

                resource = getClass().getClassLoader()
                    .getResource(RESOURCE_FILE_PREFIX + part + HTML_EXTENSION);

                String[] splitQuery = queryString.split("&");
                String account = splitQuery[0].substring(8);
                String password = splitQuery[1].substring(9);

                User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalStateException("등록되지 않은 계정입니다."));

                if (user.isMatchPassword(password)) {
                    statusCode = REDIRECTION_FOUND_CODE;
                    resource = getClass().getClassLoader()
                        .getResource(RESOURCE_FILE_PREFIX + INDEX + HTML_EXTENSION);
                    log.info("user : {}", user);
                }
                if (!user.isMatchPassword(password)) {
                    statusCode = UNAUTHORIZED_CODE;
                    resource = getClass().getClassLoader()
                        .getResource(RESOURCE_FILE_PREFIX + UNAUTHORIZED_CODE + HTML_EXTENSION);
                }
            }

            if (validateURLIsNull(resource, outputStream)) {
                return;
            }

            Path path = new File(resource.getPath()).toPath();
            byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            String response = createResponse(part, body, path, statusCode);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createResponse(String part, byte[] body, Path path, String statusCode) throws IOException {
        String contentType = resolveContentType(part);
        if (contentType == null) {
            return "";
        }

        String statusResponse = SUCCESS_STATUS_RESPONSE;
        if (!statusCode.equals(SUCCESS_CODE)) {
            statusResponse = ERROR_STATUS_RESPONSE;
        }

        return String.join("\r\n",
            "HTTP/1.1 " + statusCode + " " + statusResponse + " ",
            "Content-Type: " + contentType + ";charset=utf-8 ",
            "Content-Length: " + body.length + " ",
            "",
            new String(Files.readAllBytes(path)));
    }

    private static String resolveContentType(String part) {
        if (part.endsWith(CSS_EXTENSION)) {
            return "text/css";
        }
        if (part.endsWith(JS_EXTENSION)) {
            return "text/javascript";
        }
        if (part.endsWith(HTML_EXTENSION) || part.equals("login") || part.equals("/login")) {
            return "text/html";
        }

        return null;
    }

    private static boolean isRootRequest(String part, OutputStream outputStream)
        throws IOException {
        if (part.equals("/")) {
            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
            return true;
        }
        return false;
    }

    private static boolean validateURLIsNull(URL resource, OutputStream outputStream)
        throws IOException {
        if (resource == null) {
            String notFound = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Length: 0 ",
                "",
                "");

            outputStream.write(notFound.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            return true;
        }
        return false;
    }
}
