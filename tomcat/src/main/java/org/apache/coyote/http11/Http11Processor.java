package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_RESOURCE_PATH = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String REQUEST_LINE_ELEMENT_SEPARATOR = " ";
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final String PATH_QUERY_SEPARATOR = "?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_NAME_VALUE_SEPARATOR = "=";
    private static final String FORM_DATA_KEY_VALUE_SEPARATOR = "=";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

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
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final List<String> requestHead = readRequestHead(reader);
            String cookieHeader = extractCookieHeader(requestHead);
            HttpCookie httpCookie = new HttpCookie(cookieHeader);
            String sessionId = httpCookie.getValue("JSESSIONID");
            boolean shouldSetCookie = sessionId.isBlank();

            if (shouldSetCookie) {
                sessionId = UUID.randomUUID().toString();
            }

            int contentLength = extractContentLength(requestHead);
            String requestBody = readRequestBody(reader, contentLength);

            String response = resolveResponse(requestHead, requestBody);
            if (shouldSetCookie) {
                response = addSetCookieHeader(response, sessionId);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int extractContentLength(List<String> requestHead) {
        for (String headerLine : requestHead) {
            String[] headerParts = headerLine.split(":", 2);

            if (headerParts.length == 2
                    && headerParts[0].trim().equalsIgnoreCase("Content-Length")) {
                return Integer.parseInt(headerParts[1].trim());
            }
        }

        return 0;
    }

    private String extractCookieHeader(List<String> requestHead) {
        for (String headerLine : requestHead) {
            String[] headerParts = headerLine.split(":", 2);

            if (headerParts.length == 2
                    && headerParts[0].trim().equalsIgnoreCase("Cookie")) {
                return headerParts[1].trim();
            }
        }

        return "";
    }

    private List<String> readRequestHead(BufferedReader reader) throws IOException {
        final List<String> requestHeader = new ArrayList<>();

        while (true) {
            String currentLine = reader.readLine();
            if (currentLine == null || currentLine.isEmpty()) {
                break;
            }
            requestHeader.add(currentLine);
        }
        return requestHeader;
    }

    private String readRequestBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength < 0) {
            throw new IOException();
        }

        char[] body = new char[contentLength];
        int totalCharactersRead = 0;

        while (totalCharactersRead < contentLength) {
            int remainingCharacters = contentLength - totalCharactersRead;
            int charactersRead = reader.read(body, totalCharactersRead, remainingCharacters);

            if (charactersRead == -1) {
                throw new EOFException("");
            }
            totalCharactersRead += charactersRead;
        }

        return new String(body);
    }

    private String resolveResponse(List<String> requestHead, String requestBody) {
        final String requestLine = extractRequestLine(requestHead);
        final String requestMethod = requestLine.split(REQUEST_LINE_ELEMENT_SEPARATOR)[0];
        final String requestTarget = extractRequestTarget(requestLine);
        final String requestPath = extractTargetPath(requestTarget);

        if (requestTarget.equals(ROOT_RESOURCE_PATH)) {
            return createOkResponse(requestTarget);
        }

        if (requestPath.equals("/login")) {
            return handleLogin(requestMethod, requestBody);
        }

        if (requestPath.equals("/register")) {
            return handleRegister(requestMethod, requestBody);
        }

        try {
            return createOkResponse(requestPath);
        } catch (UncheckedServletException e) {
            return createErrorResponse();
        }
    }

    private String extractRequestLine(List<String> requestHead) {
        if (requestHead.isEmpty()) {
            return "";
        }

        return requestHead.getFirst();
    }

    private String extractRequestTarget(String requestLine) {
        return requestLine.split(REQUEST_LINE_ELEMENT_SEPARATOR)[REQUEST_TARGET_INDEX];
    }

    private String extractTargetPath(String requestTarget) {
        if (requestTarget.contains(PATH_QUERY_SEPARATOR)) {
            int delimiterIndex = requestTarget.indexOf(PATH_QUERY_SEPARATOR);
            return requestTarget.substring(0, delimiterIndex);
        }
        return requestTarget;
    }

    private String handleLogin(String requestMethod, String requestBody) {
        if (!requestMethod.equals("POST")) {
            return createOkResponse("/login.html");
        }

        Map<String, String> requestFormData = parseFormData(requestBody);

        String account = requestFormData.getOrDefault("account", "");
        String password = requestFormData.getOrDefault("password", "");

        if (areCredentialsValid(account, password)) {
            return createRedirectResponse("/index.html");
        }

        return createRedirectResponse("/401.html");
    }

    private String handleRegister(String requestMethod, String requestBody) {
        if (!requestMethod.equals("POST")) {
            return createOkResponse("/register.html");
        }

        Map<String, String> requestFormData = parseFormData(requestBody);

        if (!(requestFormData.containsKey("account")
                && requestFormData.containsKey("password")
                && requestFormData.containsKey("email"))) {
            return createOkResponse("/register.html");
        }

        User user = new User(
                requestFormData.get("account"),
                requestFormData.get("password"),
                requestFormData.get("email")
        );
        InMemoryUserRepository.save(user);

        return createRedirectResponse("/index.html");
    }

    private Map<String, String> parseFormData(String requestBody) {
        Map<String, String> formData = new HashMap<>();

        for (String field : requestBody.split("&")) {
            int separatorIndex = field.indexOf(FORM_DATA_KEY_VALUE_SEPARATOR);

            if (separatorIndex < 0) {
                continue;
            }

            String key = field.substring(0, separatorIndex);
            String value = field.substring(
                    separatorIndex + FORM_DATA_KEY_VALUE_SEPARATOR.length()
            );

            String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
            String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);

            formData.putIfAbsent(decodedKey, decodedValue);
        }

        return formData;
    }

    private String extractTargetQueryString(String requestTarget) {
        if (requestTarget.contains(PATH_QUERY_SEPARATOR)) {
            int delimiterIndex = requestTarget.indexOf(PATH_QUERY_SEPARATOR);
            return requestTarget.substring(delimiterIndex + PATH_QUERY_SEPARATOR.length());
        }

        return "";
    }

    private Map<String, String> parseQueryParameters(String targetQueryString) {
        Map<String, String> queryParameters = new HashMap<>();

        for (String queryParameter : targetQueryString.split(QUERY_PARAMETER_SEPARATOR)) {
            int separatorIndex = queryParameter.indexOf(QUERY_PARAMETER_NAME_VALUE_SEPARATOR);

            if (separatorIndex < 0) {
                continue;
            }

            String name = queryParameter.substring(0, separatorIndex);
            String value = queryParameter.substring(
                    separatorIndex + QUERY_PARAMETER_NAME_VALUE_SEPARATOR.length()
            );

            queryParameters.putIfAbsent(name, value);
        }
        return queryParameters;
    }

    private boolean areCredentialsValid(String account, String password) {
        if (account.isBlank() || password.isBlank()) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private String addSetCookieHeader(String response, String sessionId) {
        int statusLineEndIndex = response.indexOf("\r\n");
        String statusLine = response.substring(0, statusLineEndIndex);
        String remainingResponse = response.substring(statusLineEndIndex);

        return statusLine
                + "\r\nSet-Cookie: JSESSIONID=" + sessionId
                + remainingResponse;
    }

    private String createOkResponse(String resource) {
        String responseBody = resolveResponseBody(resource);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + resolveContentType(resource),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String createRedirectResponse(String resource) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + resource,
                "Content-Length: 0",
                ""
        );
    }

    private String createErrorResponse() {
        String responseBody = resolveResponseBody("/404.html");

        return String.join("\r\n",
                "HTTP/1.1 404 NotFound",
                "Content-Type: " + resolveContentType("/404.html"),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String resolveResponseBody(String resource) {
        if (resource.equals(ROOT_RESOURCE_PATH)) {
            return DEFAULT_MESSAGE;
        }

        return readResourceAsString(resource);
    }

    private String readResourceAsString(String resourceName) {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + resourceName);
        if (resource == null) {
            throw new UncheckedServletException(
                    new FileNotFoundException()
            );
        }

        try {
            URI uri = resource.toURI();

            return Files.readString(Path.of(uri));
        } catch (IOException | URISyntaxException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String resolveContentType(String content) {
        int index = content.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        if (index < 0) {
            return "text/html;charset=utf-8 ";
        }

        String extension = content.substring(index + FILE_EXTENSION_SEPARATOR.length());

        if (extension.equals("svg")) {
            return "image/svg+xml";
        }
        return "text/" + extension + ";charset=utf-8 ";
    }
}
