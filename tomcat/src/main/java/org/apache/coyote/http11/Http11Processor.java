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
import java.util.List;
import java.util.Map;
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
        try (final var bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream()) {

            final List<String> httpRequest = readHttpRequest(bufferedReader);
            handleRequest(httpRequest);
            final var response = getResponse(getUri(httpRequest));

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readHttpRequest(BufferedReader bufferedReader) throws IOException {
        List<String> httpRequest = new ArrayList<>();

        String line = bufferedReader.readLine();
        while (line != null && !line.equals("")) {
            httpRequest.add(line);
            line = bufferedReader.readLine();
        }

        return httpRequest;
    }

    private void handleRequest(List<String> httpRequest) throws IOException {
        String uri = getUri(httpRequest);
        if (defaultPathRequest(uri)) {
            return;
        }

        Map<String, Object> queryParamMap = new HashMap<>();
        URL url = handleUri(queryParamMap, uri);

        executeLogic(queryParamMap, url);
    }

    private String getUri(List<String> httpRequest) {
        return httpRequest.get(0).split(" ")[1];
    }

    private boolean defaultPathRequest(String resourcePath) {
        return resourcePath.equals("/");
    }

    private String defaultResponse() {
        final var defaultBody = DEFAULT_BODY;

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + defaultBody.getBytes().length + " ",
                "",
                defaultBody);
    }

    private URL handleUri(Map<String, Object> queryParamMap, String uri) {
        if (hasQueryString(uri)) {
            int index = uri.indexOf("?");
            handleQueryString(queryParamMap, uri, index);
        }

        return getUrl(uri);
    }

    private boolean hasQueryString(String uri) {
        return uri.contains("?");
    }

    private void handleQueryString(Map<String, Object> queryParamMap, String uri, int index) {
        String queryString = getQueryString(uri, index);
        parseQueryString(queryParamMap, queryString);
    }

    private String getQueryString(String uri, int index) {
        return uri.substring(index + 1);
    }

    private void parseQueryString(Map<String, Object> queryParamMap, String queryString) {
        String[] queryParams = queryString.split("&");
        parseQueryParams(queryParamMap, queryParams);
    }

    private void parseQueryParams(Map<String, Object> queryParamMap, String[] queryParams) {
        for (String queryParam : queryParams) {
            String[] parsedQuery = queryParam.split("=");
            queryParamMap.put(parsedQuery[0], parsedQuery[1]);
        }
    }

    private URL getUrl(String uri) {
        String resourcePath = uri.split("\\?")[0];

        if (!resourcePath.contains(".")) {
            resourcePath += ".html";
        }

        return getClass().getClassLoader().getResource("static" + resourcePath);
    }

    private void executeLogic(Map<String, Object> queryParamMap, URL url) {
        if (isLoginRequest(url, queryParamMap)) {
            findUser(queryParamMap);
        }
    }

    private boolean isLoginRequest(URL url, Map<String, Object> queryParamMap) {
        return url.getPath().contains("/login.html") && !queryParamMap.isEmpty();
    }

    private void findUser(Map<String, Object> queryParamMap) {
        InMemoryUserRepository.findByAccount((String) queryParamMap.get("account"))
                .ifPresentOrElse(
                        it -> validateUserLogin(queryParamMap, it),
                        () -> {
                            throw new IllegalArgumentException("User not found");
                        }
                );
    }

    private void validateUserLogin(Map<String, Object> queryParamMap, User it) {
        if (it.checkPassword((String) queryParamMap.get("password"))) {
            log.info(it.toString());
        }
    }

    private String getResponse(String uri) throws IOException {
        if (defaultPathRequest(uri)) {
            return defaultResponse();
        }
        return createStaticFileResponse(getUrl(uri).getPath());
    }

    private String createStaticFileResponse(String resourcePath) throws IOException {
        final Path path = Path.of(resourcePath);
        byte[] bytes = Files.readAllBytes(path);
        String mimeType = Files.probeContentType(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes));
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
