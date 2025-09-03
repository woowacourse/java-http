package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    // Resource constants
    public static final String STATIC_RESOURCE_PREFIX = "static";
    
    // Content type constants
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    public static final String CONTENT_TYPE_TEXT_CSS = "text/css";
    public static final String CONTENT_TYPE_JAVASCRIPT = "application/javascript";
    
    // File extension constants
    public static final String CSS_EXTENSION = ".css";
    public static final String JS_EXTENSION = ".js";
    public static final String HTML_EXTENSION = ".html";
    
    // HTTP constants
    public static final String HTTP_OK = "HTTP/1.1 200 OK ";
    public static final String HTTP_NOT_FOUND = "HTTP/1.1 404 Not Found";
    
    // Query parameter constants
    public static final String QUERY_SEPARATOR = "?";
    public static final String PARAM_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";
    
    // HTTP headers
    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var br = new BufferedReader(new InputStreamReader(inputStream))) {
            
            String requestUri = extractRequestUri(br);
            String resource = processRequestResource(requestUri);
            String resourceName = STATIC_RESOURCE_PREFIX + resource;
            String contentType = determineContentType(resourceName);
            
            URL resourceUrl = loadResource(resourceName);
            if (resourceUrl == null) {
                sendNotFoundResponse(outputStream);
                return;
            }
            
            String responseBody = readResourceContent(resourceUrl);
            long contentLength = calculateContentLength(resourceUrl);
            String response = buildOkResponse(contentType, contentLength, responseBody);
            
            sendResponse(outputStream, response);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String processQueryParam(int queryParamIndex, String resource, String requestUri) {
        if (queryParamIndex != -1) {
            Map<String, String> queryParams = new HashMap<>();
            resource = requestUri.substring(0, queryParamIndex) + HTML_EXTENSION;
            String queryString = requestUri.substring(queryParamIndex + 1);

            String[] splitQueryString = queryString.split(PARAM_SEPARATOR);
            for (String keyValuePair : splitQueryString) {
                String[] keyValue = keyValuePair.split(KEY_VALUE_SEPARATOR);
                if (keyValue.length >= 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                } else if (keyValue.length == 1) {
                    queryParams.put(keyValue[0], "");
                }
            }

            String account = queryParams.get("account");
            String password = queryParams.get("password");

            if (account != null && password != null) {
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저 정보입니다."));
                if (user.checkPassword(password)) {
                    log.info(user.toString());
                }
            }
        }
        return resource;
    }

    private String extractRequestUri(BufferedReader br) throws IOException {
        String firstLine = br.readLine();
        String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[1];
    }

    private String processRequestResource(String requestUri) {
        String resource = requestUri;
        int queryParamIndex = requestUri.indexOf(QUERY_SEPARATOR);
        return processQueryParam(queryParamIndex, resource, requestUri);
    }

    private String determineContentType(String resourceName) {
        if (resourceName.contains(CSS_EXTENSION)) {
            return CONTENT_TYPE_TEXT_CSS;
        }
        if (resourceName.contains(JS_EXTENSION)) {
            return CONTENT_TYPE_JAVASCRIPT;
        }
        return CONTENT_TYPE_TEXT_HTML;
    }

    private URL loadResource(String resourceName) {
        URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        if (resourceUrl == null) {
            resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        return resourceUrl;
    }

    private void sendNotFoundResponse(java.io.OutputStream outputStream) throws IOException {
        final var notFoundResponse = String.join(HTTP_LINE_SEPARATOR,
                HTTP_NOT_FOUND,
                HEADER_CONTENT_TYPE + CONTENT_TYPE_TEXT_HTML,
                HEADER_CONTENT_LENGTH + "0",
                "",
                "");
        outputStream.write(notFoundResponse.getBytes());
        outputStream.flush();
    }

    private String readResourceContent(URL resourceUrl) throws IOException, URISyntaxException {
        Path path = Path.of(resourceUrl.toURI());
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private long calculateContentLength(URL resourceUrl) throws IOException, URISyntaxException {
        Path path = Path.of(resourceUrl.toURI());
        return Files.size(path);
    }

    private String buildOkResponse(String contentType, long contentLength, String responseBody) {
        return String.join(HTTP_LINE_SEPARATOR,
                HTTP_OK,
                HEADER_CONTENT_TYPE + contentType + " ",
                HEADER_CONTENT_LENGTH + contentLength + " ",
                "",
                responseBody);
    }

    private void sendResponse(java.io.OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
