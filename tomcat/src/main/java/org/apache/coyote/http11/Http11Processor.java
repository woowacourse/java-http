package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int REQUEST_RESOURCE_POSITION = 1;
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final String ROOT_PATH = "/";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String EXTENSION_DELIMITER_REGEX = "\\.";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String STATUS_CODE_200 = "200 OK";
    private static final String STATUS_CODE_302 = "302 Found";
    private static final String STATUS_CODE_401 = "401 Unauthorized";
    private static final String CONTENT_TYPE_HEADER = "Content-Type: ";
    private static final String TEXT_TYPE_PREFIX = "text/";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    private static final String LOCATION_HEADER = "Location: ";

    private final Socket connection;

    public Http11Processor(Socket connection) {
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
            BufferedReader request = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = request.readLine();
            System.out.println(firstLine);
            String resource = "";
            String response = "";
            if (firstLine.split(" ")[0].equals("GET") && !parseRequestResource(firstLine).contains("/login")) {
                resource = parseRequestResource(firstLine);
                response = getResponse(resource);
            }

            if (firstLine.split(" ")[0].equals("GET") && parseRequestResource(firstLine).contains("/login")) {
                String line;
                while (!(line = request.readLine()).isEmpty()) {
                    if (line.contains("Cookie")) {
                        String uuid = "";
                        for (String v : line.split(";")) {
                            if (v.contains("JSESSIONID")) {
                                uuid = v.split("=")[1];
                            }
                        }
                        if (Session.containsSession(uuid)) {
                            response = String.join("\r\n",
                                    "HTTP/1.1 302 Found ",
                                    "Location: " + "/index.html");
                        } else {
                            resource = parseRequestResource(firstLine);
                            response = getResponse(resource);
                        }
                    } else {
                        resource = parseRequestResource(firstLine);
                        response = getResponse(resource);
                    }
                }
            }

            if (firstLine.split(" ")[0].equals("POST")) {
                String line;
                int contentLength = 0;
                while (!(line = request.readLine()).isEmpty()) {
                    if (line.contains("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(" ")[1]);
                    }
                }

                char[] buffer = new char[contentLength];
                request.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                if (parseRequestResource(firstLine).contains("login")) {
                    String account = requestBody.split("&")[0].split("=")[1];
                    String password = requestBody.split("&")[1].split("=")[1];
                    if (findUserByInfo(account, password)) {
                        UUID uuid = UUID.randomUUID();
                        response = String.join("\r\n",
                                "HTTP/1.1 302 Found ",
                                "Location: " + "/index.html",
                                "Set-Cookie: JSESSIONID=" + uuid);
                        Session.save(uuid.toString(), InMemoryUserRepository.findByAccount(account).get());
                    }

                    if (!findUserByInfo(account, password)) {
                        response = String.join("\r\n",
                                "HTTP/1.1 302 Found ",
                                "Location: " + "/401.html");
                    }
                }

                if (parseRequestResource(firstLine).contains("register")) {
                    String account = requestBody.split("&")[0].split("=")[1];
                    String email = requestBody.split("&")[1].split("=")[1];
                    String password = requestBody.split("&")[2].split("=")[1];
                    User user = new User(account, password, email);
                    InMemoryUserRepository.save(user);
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: " + "/index.html");
                }

            }
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestResource(String firstLine) throws IOException {
        return firstLine.split(" ")[REQUEST_RESOURCE_POSITION];
    }

    private String getResponse(String resource) throws IOException {
        String statusCode = STATUS_CODE_200;
        String responseBody = DEFAULT_RESPONSE_BODY;
        String extension = DEFAULT_EXTENSION;

        if (resource.contains(QUERY_PARAM_DELIMITER)) {
            Result result = handleQueryParameters(resource);
            resource = result.resourceWithExtension;
            statusCode = result.statusCode;
        }

        if (!resource.equals(ROOT_PATH)) {
            resource = addExtensionWhenDoesNotExists(resource);
            responseBody = loadResourceContent(resource);
            extension = getExtension(resource);
        }

        return joinResponse(statusCode, responseBody, extension);
    }

    private Result handleQueryParameters(String resource) {
        int index = resource.indexOf(QUERY_PARAM_DELIMITER);
        String resourcePage = resource.substring(0, index);
        String queryString = resource.substring(index + 1);
        String statusCode = STATUS_CODE_200;

        if (isLoginPage(resourcePage)) {
            statusCode = getStatusCodeByLoginPageResponse(queryString, statusCode);
        }

        String resourceWithExtension = resourcePage + "." + DEFAULT_EXTENSION;

        return new Result(resourceWithExtension, statusCode);
    }

    private String getStatusCodeByLoginPageResponse(String queryString, String statusCode) {
        if (loginPageResponse(queryString)) {
            statusCode = STATUS_CODE_302;
        }

        if (!loginPageResponse(queryString)) {
            statusCode = STATUS_CODE_401;
        }

        return statusCode;
    }

    private record Result(String resourceWithExtension, String statusCode) {
    }

    private boolean isLoginPage(String resourcePage) {
        return resourcePage.contains("/login");
    }

    private boolean loginPageResponse(String queryString) {
        String account = extractQueryParameter(queryString, "account");
        String password = extractQueryParameter(queryString, "password");
        return findUserByInfo(account, password);
    }

    private String extractQueryParameter(String queryString, String parameter) {
        for (String pair : queryString.split(PARAMETER_DELIMITER)) {
            String[] keyValue = pair.split(KEY_VALUE_DELIMITER);
            if (keyValue[0].equals(parameter) && keyValue.length > 1) {
                return keyValue[1];
            }
        }
        return "";
    }

    private boolean findUserByInfo(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info(user.toString());
                    return true;
                })
                .orElse(false);
    }

    private String addExtensionWhenDoesNotExists(String resource) {
        if (hasNotExtension(resource)) {
            resource += "." + DEFAULT_EXTENSION;
        }

        return resource;
    }

    private String loadResourceContent(String resource) throws IOException {
        String resourcePath = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(STATIC_RESOURCE_PATH + resource))
                .getPath();

        try (FileInputStream file = new FileInputStream(resourcePath)) {
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private boolean hasNotExtension(String resource) {
        return !resource.contains(".");
    }

    private String getExtension(String resource) {
        String[] parts = resource.split(EXTENSION_DELIMITER_REGEX);
        return parts[parts.length - 1];
    }

    private String joinResponse(String statusCode, String responseBody, String extension) {
        String redirectHeader = getRedirectHeader(statusCode);

        List<String> headers = new ArrayList<>();
        headers.add(HTTP_VERSION + " " + statusCode + " ");
        headers.add(CONTENT_TYPE_HEADER + TEXT_TYPE_PREFIX + extension + CHARSET_UTF_8 + " ");
        headers.add(CONTENT_LENGTH_HEADER + responseBody.getBytes().length + " ");

        if (!redirectHeader.isBlank()) {
            headers.set(0, HTTP_VERSION + " " + "302" + " ");
            headers.add(redirectHeader + " ");
        }

        return String.join("\r\n", headers) + "\r\n\r\n" + responseBody;
    }

    private String getRedirectHeader(String statusCode) {
        String redirectHeader = "";

        if (STATUS_CODE_302.equals(statusCode)) {
            redirectHeader = LOCATION_HEADER + "/index.html";
        }

        if (STATUS_CODE_401.equals(statusCode)) {
            redirectHeader = LOCATION_HEADER + "/401.html";
        }

        return redirectHeader;
    }
}
