package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HTTP_METHOD = "HTTP_METHOD";
    private static final String URL = "URL";
    private static final String HTTP_VERSION = "HTTP_VERSION";
    private static final Map<String, String> CONTENT_TYPE;
    private static final Map<String, String> HTTP_STATUS;

    private final Socket connection;

    static {
        final Map<String, String> contentType = new HashMap<>();
        contentType.put("html", "text/html;charset=utf-8");
        contentType.put("css", "text/css");
        contentType.put("js", "application/javascript");
        contentType.put("ico", "image/x-icon");
        CONTENT_TYPE = Collections.unmodifiableMap(contentType);
        final Map<String, String> httpStatus = new HashMap<>();
        httpStatus.put("OK", "200 OK");
        httpStatus.put("NOT_FOUND", "404 NOT_FOUND");
        HTTP_STATUS = Collections.unmodifiableMap(httpStatus);
    }

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
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final Map<String, String> requestLine = extractRequestLine(reader);
            final Map<String, String> httpHeaders = extractRequestHeaders(reader);
            final String method = requestLine.get(HTTP_METHOD);
            final String path = parsingPath(requestLine.get(URL));
            final Map<String, String> parsingQueryString = parsingQueryString(requestLine.get(URL));

            final String response = controller(method, path, parsingQueryString);
            outputStream.write(response.getBytes());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String controller(final String method, final String path, final Map<String, String> queryString) throws IOException {
        if ("GET".equals(method) && "/".equals(path)) {
            return generateResponseBody("OK", "html", "Hello world!");
        }
        if ("GET".equals(method) && "/index.html".equals(path)) {
            return generateResult(path, "OK");
        }
        if ("GET".equals(method) && "/login".equals(path)) {
            final User user = InMemoryUserRepository.findByAccount(queryString.get("account"))
                    .orElseThrow(IllegalArgumentException::new);
            if (user.checkPassword(queryString.get("password"))) {
                log.info("user : {}", user);
            }
            return generateResult(path, "OK");
        }
        return generateResult(path, "OK");
    }

    private String generateResult(final String path, final String statusCode) throws IOException {
        final String resourcePath = viewResolve(path);
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (Objects.isNull(resource)) {
            return "";
        }
        final Path staticFilePath = new File(resource.getPath()).toPath();
        final int lastDotIndex = staticFilePath.toString().lastIndexOf(".");
        final String fileExtension = staticFilePath.toString().substring(lastDotIndex + 1);
        final String responseBody = Files.readString(staticFilePath);

        return generateResponseBody(statusCode, fileExtension, responseBody);
    }

    private String generateResponseBody(final String statusCode, final String fileExtension, final String responseBody) {
        return String.join(System.lineSeparator(),
                "HTTP/1.1 " + HTTP_STATUS.get(statusCode) + " ",
                "Content-Type: " + CONTENT_TYPE.get(fileExtension) + " ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private String viewResolve(final String resourcePath) {
        if (!resourcePath.contains(".") && !resourcePath.endsWith(".html")) {
            return resourcePath + ".html";
        }

        return resourcePath;
    }

    private Map<String, String> parsingQueryString(final String path) {
        final Map<String, String> queryStrings = new HashMap<>();
        if (path.contains("?")) {
            final String queryString = path.substring(path.indexOf("?") + 1);
            Arrays.stream(queryString.split("&")).forEach(query -> {
                String[] keyValue = query.split("=");
                queryStrings.put(keyValue[0], keyValue[1]);
            });
        }

        return queryStrings;
    }

    private String parsingPath(final String path) {
        if (path.contains("?")) {
            return path.substring(0, path.indexOf("?"));
        }

        return path;
    }

    private Map<String, String> extractRequestLine(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        final String[] parts = requestLine.split(" ");

        final Map<String, String> request = new HashMap<>();
        request.put(HTTP_METHOD, parts[0]);
        request.put(URL, parts[1]);
        request.put(HTTP_VERSION, parts[2]);

        return request;
    }

    private Map<String, String> extractRequestHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> requests = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final int colonIndex = line.indexOf(": ");
            final String key = line.substring(0, colonIndex);
            final String value = line.substring(colonIndex + 2);
            requests.put(key, value);
        }

        return requests;
    }
}
