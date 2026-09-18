package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String OK = "HTTP/1.1 200 OK ";
    private static final String NOT_FOUND = "HTTP/1.1 404 Not Found ";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_BODY = "Hello world!";
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String LOGIN_PATH = "/login";
    private static final String ROOT_PATH = "/";

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = bufferedReader.readLine();
            final String response = createResponse(requestLine);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String requestLine) throws IOException {
        if (requestLine == null) {
            return buildResponse(OK, DEFAULT_CONTENT_TYPE, DEFAULT_BODY);
        }

        final String uri = requestLine.split(" ")[1];
        final String path = parsePath(uri);
        final String queryString = parseQueryString(uri);

        login(path, queryString);

        return createResourceResponse(path);
    }

    private String parsePath(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private String parseQueryString(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return "";
        }
        return uri.substring(index + 1);
    }

    private void login(final String path, final String queryString) {
        if (!path.equals(LOGIN_PATH) || queryString.isEmpty()) {
            return;
        }

        final Map<String, String> params = parseParams(queryString);
        InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(user -> user.checkPassword(params.get("password")))
                .ifPresent(user -> log.info("{}", user));
    }

    private String createResourceResponse(final String path) throws IOException {
        if (path.equals(ROOT_PATH)) {
            return buildResponse(OK, DEFAULT_CONTENT_TYPE, DEFAULT_BODY);
        }

        final String resourcePath = toResourcePath(path);
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            final URL notFound = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            return buildResponse(NOT_FOUND, DEFAULT_CONTENT_TYPE, readResource(notFound));
        }

        return buildResponse(OK, getContentType(resourcePath), readResource(resource));
    }

    private String buildResponse(final String statusLine, final String contentType, final String body) {
        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript";
        }
        return DEFAULT_CONTENT_TYPE;
    }

    private String readResource(final URL resource) throws IOException {
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String toResourcePath(final String path) {
        if (path.contains(".")) {
            return "static" + path;
        }
        return "static" + path + ".html";
    }

    private Map<String, String> parseParams(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        for (final String parameter : queryString.split("&")) {
            final String[] keyAndValue = parameter.split("=", 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return params;
    }
}
