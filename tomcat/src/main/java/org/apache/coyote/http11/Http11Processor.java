package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
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
    private static final String OK_STATUS = "200 OK";
    private static final String CONTENT_TYPE_HEADER = "Content-Type: ";
    private static final String TEXT_TYPE_PREFIX = "text/";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";

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
            String resource = parseRequestResource(inputStream);
            String response = getResponse(resource);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestResource(InputStream inputStream) throws IOException {
        BufferedReader request = new BufferedReader(new InputStreamReader(inputStream));
        String firstLine = request.readLine();
        return firstLine.split(" ")[REQUEST_RESOURCE_POSITION];
    }

    private String getResponse(String resource) throws IOException {
        String responseBody = DEFAULT_RESPONSE_BODY;
        String extension = DEFAULT_EXTENSION;

        if (resource.contains(QUERY_PARAM_DELIMITER)) {
            resource = handleQueryParameters(resource);
        }

        if (!resource.equals(ROOT_PATH)) {
            responseBody = loadResourceContent(resource);
            extension = getExtension(resource);
        }

        return joinResponse(responseBody, extension);
    }

    private String handleQueryParameters(String resource) {
        int index = resource.indexOf(QUERY_PARAM_DELIMITER);
        String resourcePage = resource.substring(0, index);
        String queryString = resource.substring(index + 1);

        if (isLoginPage(resourcePage)) {
            loginPageResponse(queryString);
        }

        return resourcePage + "." + DEFAULT_EXTENSION;
    }

    private boolean isLoginPage(String resourcePage) {
        return resourcePage.equals("/login");
    }

    private void loginPageResponse(String queryString) {
        String account = extractQueryParameter(queryString, "account");
        String password = extractQueryParameter(queryString, "password");
        findUserByInfo(account, password);
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

    private static void findUserByInfo(String account, String password) {
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info(user.toString()));
    }

    private String loadResourceContent(String resource) throws IOException {
        String resourcePath = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(STATIC_RESOURCE_PATH + resource))
                .getPath();

        try (FileInputStream file = new FileInputStream(resourcePath)) {
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String getExtension(String resource) {
        String[] parts = resource.split(EXTENSION_DELIMITER_REGEX);
        return parts[parts.length - 1];
    }

    private static String joinResponse(String responseBody, String extension) {
        return String.join("\r\n",
                HTTP_VERSION + " " + OK_STATUS + " ",
                CONTENT_TYPE_HEADER + TEXT_TYPE_PREFIX + extension + CHARSET_UTF_8 + " ",
                CONTENT_LENGTH_HEADER + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
