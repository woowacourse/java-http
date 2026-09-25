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
import java.net.URLDecoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
    public static final String FOUND_STATUS_RESPONSE = "Found";
    public static final String INDEX = "index";
    public static final String INDEX_PAGE = "/index.html";
    public static final String UNAUTHORIZED_STATUS_RESPONSE = "Unauthorized";

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
            if (requestLine == null) {
                return;
            }

            final Map<String, String> requestHeaders = new HashMap<>();
            String line;
            while (!(line = bufferedReader.readLine()).isEmpty()) {
                String[] parsedLine = line.split(":", 2);
                if (parsedLine.length != 2) {
                    continue;
                }

                requestHeaders.put(parsedLine[0].strip(), parsedLine[1].strip());
            }

            if (isPostRequest(requestHeaders, bufferedReader, outputStream)) {
                return;
            }

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

            if (requestUri.equals("register")) {
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

                if (loginSuccess(user, password, outputStream)) {
                    return;
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

            writeAndFlush(outputStream, response.getBytes());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean loginSuccess(User user, String password, OutputStream outputStream)
        throws IOException {
        if (user.isMatchPassword(password)) {
            log.info("user : {}", user);
            sendRedirect(outputStream, INDEX_PAGE);
            return true;
        }
        return false;
    }

    private static boolean isPostRequest(Map<String, String> requestHeaders,
        BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        if (requestHeaders.get("Content-Length") != null) {
            int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);

            Map<String, String> formData = parseFormData(requestBody);
            User user = new User(
                formData.get("account"),
                formData.get("password"),
                formData.get("email"));

            InMemoryUserRepository.save(user);
            log.info("register user: {}", user);


            sendRedirect(outputStream, INDEX_PAGE);
            return true;
        }
        return false;
    }

    private static void sendRedirect(OutputStream outputStream, String location)
        throws IOException {
        String response = String.join("\r\n",
            "HTTP/1.1 " + REDIRECTION_FOUND_CODE + " " + FOUND_STATUS_RESPONSE + " ",
            "Set-Cookie: JSESSIONID=" + UUID.randomUUID(),
            "Location: " + location + " ",
            "Content-Length: 0 ",
            "",
            "");

        writeAndFlush(outputStream, response.getBytes(StandardCharsets.UTF_8));
    }

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> parsed = new HashMap<>();
        for (String pair : formData.split("&")) {
            String[] keyAndValue = pair.split("=", 2);
            if (keyAndValue.length != 2) {
                continue;
            }

            parsed.put(
                URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8),
                URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8));
        }
        return parsed;
    }

    private static void writeAndFlush(OutputStream outputStream, byte[] response)
        throws IOException {
        outputStream.write(response);
        outputStream.flush();
    }

    private static String createResponse(String part, byte[] body, Path path, String statusCode) throws IOException {
        String contentType = resolveContentType(part);
        if (contentType == null) {
            return "";
        }

        String statusResponse = SUCCESS_STATUS_RESPONSE;
        if (statusCode.equals(UNAUTHORIZED_CODE)) {
            statusResponse = UNAUTHORIZED_STATUS_RESPONSE;
        }
        if (statusCode.equals(REDIRECTION_FOUND_CODE)) {
            statusResponse = FOUND_STATUS_RESPONSE;
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
        if (part.endsWith(HTML_EXTENSION) || part.equals("login") || part.equals("/login") || part.equals("/register")) {
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

            writeAndFlush(outputStream, response.getBytes());
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

            writeAndFlush(outputStream, notFound.getBytes(StandardCharsets.UTF_8));
            return true;
        }
        return false;
    }
}
