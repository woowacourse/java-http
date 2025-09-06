package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DynamicRequestProcessor {

    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HEADER_LOCATION = "Location: ";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";

    public static final String QUERY_PARAM_STARTER = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    
    public static void processDynamic(String requestUri, OutputStream outputStream) throws IOException, URISyntaxException {
        Map<String, String> queryParams = parseQueryParams(requestUri);
        if (hasAuthParams(queryParams)) {
            try {
                AuthHandler.authenticate(queryParams);
                String redirectResponse = buildRedirectResponse("/index.html");
                sendResponse(outputStream, redirectResponse);
            } catch (IllegalArgumentException e) {
                send401Page(outputStream);
            }
        } else {
            String resourcePath;
            if (requestUri.equals("/")) {
                resourcePath = "/index.html";
            } else {
                resourcePath = requestUri + HTML_EXTENSION;
            }
            StaticResourceProcessor.processStatic(resourcePath, outputStream);
        }
    }

    private static Map<String, String> parseQueryParams(String requestUri) {
        int queryIndex = requestUri.indexOf(QUERY_PARAM_STARTER);

        Map<String, String> params = new HashMap<>();
        if (queryIndex != -1) {
            String queryString = requestUri.substring(queryIndex + 1);
            String[] pairs = queryString.split(PARAM_SEPARATOR);
            for (String pair : pairs) {
                String[] keyValue = pair.split(KEY_VALUE_SEPARATOR);
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    private static boolean hasAuthParams(Map<String, String> queryParams) {
        return queryParams.containsKey("account") && queryParams.containsKey("password");
    }

    private static String buildRedirectResponse(String location) {
        return String.join(HTTP_LINE_SEPARATOR,
                HttpStatus.FOUND.getStatusLine(),
                HEADER_LOCATION + location,
                HEADER_CONTENT_LENGTH + "0",
                "",
                "");
    }

    private static void send401Page(OutputStream outputStream) throws IOException, URISyntaxException {
        URI resourceUri = DynamicRequestProcessor.class.getClassLoader().getResource("static/401.html").toURI();
        Path path = Path.of(resourceUri);
        byte[] bodyBytes = Files.readAllBytes(path);
        String response = String.join(HTTP_LINE_SEPARATOR,
                HttpStatus.UNAUTHORIZED.getStatusLine(),
                HEADER_CONTENT_TYPE + "text/html;charset=utf-8",
                HEADER_CONTENT_LENGTH + bodyBytes.length,
                "",
                Files.readString(path, StandardCharsets.UTF_8));
        sendResponse(outputStream, response);
    }

    private static void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
