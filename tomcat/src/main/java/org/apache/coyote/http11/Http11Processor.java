package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            final String requestLine = extractRequestLine(requestHead);

            final String requestTarget = extractRequestTarget(requestLine);

            String responseBody = resolveResponseBody(requestTarget);
            String contentType = resolveContentType(requestTarget);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
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

    private String extractRequestLine(List<String> requestHead) {
        if (requestHead.isEmpty()) {
            return "";
        }

        return requestHead.getFirst();
    }

    private String extractRequestTarget(String requestLine) {
        return requestLine.split(REQUEST_LINE_ELEMENT_SEPARATOR)[REQUEST_TARGET_INDEX];
    }

    private String resolveResponseBody(String requestTarget) {
        if (requestTarget.equals(ROOT_RESOURCE_PATH)) {
            return DEFAULT_MESSAGE;
        }
        String targetPath = extractTargetPath(requestTarget);

        if (targetPath.equals("/login")) {
            handleLogin(requestTarget);

            return readResourceAsString("/login.html");
        }

        return readResourceAsString(targetPath);
    }

    private String extractTargetPath(String requestTarget) {
        if (requestTarget.contains(PATH_QUERY_SEPARATOR)) {
            int delimiterIndex = requestTarget.indexOf(PATH_QUERY_SEPARATOR);
            return requestTarget.substring(0, delimiterIndex);
        }
        return requestTarget;
    }

    private void handleLogin(String requestTarget) {
        String targetQueryString = extractTargetQueryString(requestTarget);
        Map<String, String> queryParameters = parseQueryParameters(targetQueryString);

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info(String.valueOf(user)));
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

            queryParameters.put(name, value);
        }
        return queryParameters;
    }

    private String readResourceAsString(String resourceName) {
        try {
            URI uri = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + resourceName).toURI();

            return Files.readString(Path.of(uri));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String resolveContentType(String targetPath) {
        if (targetPath.contains(".")) {
            if (targetPath.split("\\.")[1].equals("html")) {
                return "text/html;charset=utf-8 ";
            }
            if (targetPath.split("\\.")[1].equals("css")) {
                return "text/css;charset=utf-8 ";
            }
            if (targetPath.split("\\.")[1].equals("js")) {
                return "text/js;charset=utf-8 ";
            }
        }
        return "text/html;charset=utf-8 ";
    }
}
