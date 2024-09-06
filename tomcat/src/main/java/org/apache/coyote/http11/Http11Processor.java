package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.UserService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String SUCCESS_STATUS_CODE = "200 OK";
    private static final String STATIC_PATH_PREFIX = "static";
    private static final String TEXT_CONTENT_TYPE_PREFIX = "text/";
    private static final String HTML_TYPE = ".html";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final UserService userService;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.userService = new UserService();
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
            final Http11Request request = Http11Parser.readHttpRequest(bufferedReader);
            final Http11Response response = handle(request);
            String serializedResponse = response.serializeResponse();
            outputStream.write(serializedResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response handle(Http11Request request) {
        final String requestURI = request.getRequestURI();
        final Http11Method httpMethod = request.getHttpMethod();

        if (Http11Method.GET.equals(httpMethod)) {
            Map<String, String> queryString = parseQueryString(requestURI);
            if (requestURI.contains("/login")) {
                userService.login(queryString.get("account"), queryString.get("password"));
                String type = parseTextContentType(HTML_TYPE);
                String path = parseStaticPath("/login.html");
                return new Http11Response(SUCCESS_STATUS_CODE, type, path);
            }
            if (requestURI.contains(".")) {
                String type = parseTextContentType(requestURI);
                String path = parseStaticPath(requestURI);
                return new Http11Response(SUCCESS_STATUS_CODE, type, path);
            }
            String type = parseTextContentType(HTML_TYPE);
            return new Http11Response(SUCCESS_STATUS_CODE, type, requestURI);
        }
        if (Http11Method.POST.equals(httpMethod)) {
            String type = parseTextContentType(HTML_TYPE);
            return new Http11Response(SUCCESS_STATUS_CODE, type, requestURI);
        }
        throw new IllegalArgumentException("지원하지 않는 기능입니다.");
    }

    private Map<String, String> parseQueryString(String requestURI) {
        Map<String, String> queries = new HashMap<>();
        if (requestURI.contains("?")) {
            int index = requestURI.indexOf("?");
            String queryString = requestURI.substring(index + 1);
            for (String eachQueryString : queryString.split("&")) {
                String[] parsedEachQueryString = eachQueryString.split("=");
                queries.put(parsedEachQueryString[0], parsedEachQueryString[1]);
            }
        }
        return queries;
    }

    private String parseTextContentType(String filePath) {
        return TEXT_CONTENT_TYPE_PREFIX + filePath.split("\\.")[1];
    }

    private String parseStaticPath(String filePath) {
        return STATIC_PATH_PREFIX + filePath;
    }
}
