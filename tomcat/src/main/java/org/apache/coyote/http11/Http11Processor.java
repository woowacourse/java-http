package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_BODY = "Hello world!";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                StandardCharsets.UTF_8)); final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequest.of(readHttpRequest(bufferedReader));
            handleRequest(httpRequest);
            final var response = getResponse(httpRequest.getPath());

            writeResponse(outputStream, response);
        } catch (final IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Queue<String> readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final Queue<String> httpRequest = new LinkedList<>();

        String line = bufferedReader.readLine();
        while (line != null && !line.equals("")) {
            httpRequest.add(line);
            line = bufferedReader.readLine();
        }

        return httpRequest;
    }

    private void handleRequest(final HttpRequest httpRequest) throws IOException {
        final String path = httpRequest.getPath();

        if (!defaultPathRequest(path)) {
            executeLogic(httpRequest.getQueryParams(), getUrl(path));
        }
    }

    private String getUri(final List<String> httpRequest) {
        return httpRequest.get(0).split(" ")[1];
    }

    private boolean defaultPathRequest(final String resourcePath) {
        return resourcePath.equals("/");
    }

    private String defaultResponse() {
        final var defaultBody = DEFAULT_BODY;

        return String.join("\r\n", "HTTP/1.1 200 OK ", "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + defaultBody.getBytes().length + " ", "", defaultBody);
    }

    private URL handleUri(final Map<String, Object> queryParamMap, final String uri) {
        if (hasQueryString(uri)) {
            final int index = uri.indexOf("?");
            handleQueryString(queryParamMap, uri, index);
        }

        return getUrl(uri);
    }

    private boolean hasQueryString(final String uri) {
        return uri.contains("?");
    }

    private void handleQueryString(final Map<String, Object> queryParamMap, final String uri, final int index) {
        final String queryString = getQueryString(uri, index);
        parseQueryString(queryParamMap, queryString);
    }

    private String getQueryString(final String uri, final int index) {
        return uri.substring(index + 1);
    }

    private void parseQueryString(final Map<String, Object> queryParamMap, final String queryString) {
        final String[] queryParams = queryString.split("&");
        parseQueryParams(queryParamMap, queryParams);
    }

    private void parseQueryParams(final Map<String, Object> queryParamMap, final String[] queryParams) {
        for (final String queryParam : queryParams) {
            final String[] parsedQuery = queryParam.split("=");
            queryParamMap.put(parsedQuery[0], parsedQuery[1]);
        }
    }

    private URL getUrl(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        return getClass().getClassLoader().getResource("static" + path);
    }

    private void executeLogic(final Map<String, String> queryParamMap, final URL url) {
        if (isLoginRequest(url, queryParamMap)) {
            findUser(queryParamMap);
        }
    }

    private boolean isLoginRequest(final URL url, final Map<String, String> queryParamMap) {
        return url.getPath().contains("/login") && !queryParamMap.isEmpty();
    }

    private void findUser(final Map<String, String> queryParamMap) {
        InMemoryUserRepository.findByAccount((String) queryParamMap.get("account"))
                .ifPresentOrElse(it -> validateUserLogin(queryParamMap, it), () -> {
                    throw new IllegalArgumentException("User not found");
                });
    }

    private void validateUserLogin(final Map<String, String> queryParamMap, final User it) {
        if (it.checkPassword((String) queryParamMap.get("password"))) {
            log.info(it.toString());
        }
    }

    private String getResponse(final String uri) throws IOException {
        if (defaultPathRequest(uri)) {
            return defaultResponse();
        }
        return createStaticFileResponse(getUrl(uri).getPath());
    }

    private String createStaticFileResponse(final String resourcePath) throws IOException {
        final Path path = Path.of(resourcePath);
        final byte[] bytes = Files.readAllBytes(path);
        final String mimeType = Files.probeContentType(path);

        return String.join("\r\n", "HTTP/1.1 200 OK ", "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ", "", new String(bytes));
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
