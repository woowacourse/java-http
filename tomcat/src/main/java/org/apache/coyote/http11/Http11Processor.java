package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_RESOURCE_PATH = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String PATH_QUERY_SEPARATOR = "?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_NAME_VALUE_SEPARATOR = "=";
    private static final String FORM_DATA_KEY_VALUE_SEPARATOR = "=";
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final int READ_TIMEOUT_MILLISECONDS = 5000;
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
        ) {
            connection.setSoTimeout(READ_TIMEOUT_MILLISECONDS);

            HttpRequest request = new HttpRequest(inputStream);
            String sessionId = request.getCookie("JSESSIONID");

            boolean shouldSetCookie = false;
            SessionManager sessionManager = new SessionManager();
            Session session = sessionManager.findSession(sessionId);
            if (session == null) {
                shouldSetCookie = true;
                sessionId = UUID.randomUUID().toString();
                session = new Session(sessionId);

                sessionManager.add(session);
            }

            String response = resolveResponse(request, session);
            if (shouldSetCookie) {
                response = addSetCookieHeader(response, session);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (SocketTimeoutException exception) {
            log.warn("소켓에서 데이터 읽기를 기다리는 시간이 초과되었습니다.");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveResponse(HttpRequest request, Session session) {
        final RequestLine requestLine = request.getRequestLine();
        final String requestBody = request.getRequestBody();
        final String requestMethod = requestLine.getMethod();
        final String requestTarget = requestLine.getRequestTarget();
        final String requestPath = extractTargetPath(requestTarget);

        if (requestTarget.equals(ROOT_RESOURCE_PATH)) {
            return createOkResponse(requestTarget);
        }

        if (requestPath.equals("/login")) {
            return handleLogin(requestMethod, requestBody, session);
        }

        if (requestPath.equals("/register")) {
            return handleRegister(requestMethod, requestBody, session);
        }

        try {
            return createOkResponse(requestPath);
        } catch (UncheckedServletException e) {
            return createErrorResponse();
        }
    }

    private String extractTargetPath(String requestTarget) {
        if (requestTarget.contains(PATH_QUERY_SEPARATOR)) {
            int delimiterIndex = requestTarget.indexOf(PATH_QUERY_SEPARATOR);
            return requestTarget.substring(0, delimiterIndex);
        }
        return requestTarget;
    }

    private String handleLogin(String requestMethod, String requestBody, Session session) {
        User loggedInUser = (User) session.getAttribute("user");

        if (requestMethod.equals("GET") && loggedInUser != null) {
            return createRedirectResponse("/index.html");
        }

        if (!requestMethod.equals("POST")) {
            return createOkResponse("/login.html");
        }

        Map<String, String> requestFormData = parseFormData(requestBody);

        String account = requestFormData.getOrDefault("account", "");
        String password = requestFormData.getOrDefault("password", "");

        if (areCredentialsValid(account, password)) {
            User user = InMemoryUserRepository.findByAccount(account).get();
            session.setAttribute("user", user);
            return createRedirectResponse("/index.html");
        }

        return createRedirectResponse("/401.html");
    }

    private String handleRegister(String requestMethod, String requestBody, Session session) {
        User loggedInUser = (User) session.getAttribute("user");

        if (requestMethod.equals("GET") && loggedInUser != null) {
            return createRedirectResponse("/index.html");
        }

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

    private String addSetCookieHeader(String response, Session session) {
        int statusLineEndIndex = response.indexOf("\r\n");
        String statusLine = response.substring(0, statusLineEndIndex);
        String remainingResponse = response.substring(statusLineEndIndex);

        return statusLine
                + "\r\nSet-Cookie: JSESSIONID=" + session.getId()
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
