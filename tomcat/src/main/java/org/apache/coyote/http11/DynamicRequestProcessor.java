package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DynamicRequestProcessor {

    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HEADER_LOCATION = "Location: ";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";

    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    public static final String INDEX_HTML = "/index.html";

    public static void processDynamic(String httpMethod, String requestUri, String body, HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        if ("POST".equals(httpMethod)) {
            Map<String, String> formData = parseFormData(body);
            if (requestUri.equals("/register")) {
                handleRegister(formData, httpCookie, outputStream);
            } else if (requestUri.equals("/login")) {
                handleLogin(formData, httpCookie, outputStream);
            }
        } else {
            String resourcePath;
            if (requestUri.equals("/")) {
                resourcePath = INDEX_HTML;
            } else {
                resourcePath = requestUri;
            }
            StaticResourceProcessor.processStatic(resourcePath, outputStream);
        }
    }

    private static void handleRegister(Map<String, String> formData, HttpCookie httpCookie, OutputStream outputStream) throws IOException {
        AuthHandler.register(formData);
        String redirectResponse = buildAuthRedirectResponse(INDEX_HTML, httpCookie);
        sendResponse(outputStream, redirectResponse);
    }

    private static void handleLogin(Map<String, String> formData, HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        try {
            AuthHandler.authenticate(formData);
            String redirectResponse = buildAuthRedirectResponse(INDEX_HTML, httpCookie);
            sendResponse(outputStream, redirectResponse);
        } catch (IllegalArgumentException e) {
            send401Page(outputStream);
        }
    }

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split(PARAM_SEPARATOR);
        for (String pair : pairs) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0].strip(), keyValue[1].strip());
            }
        }
        return params;
    }

    private static String buildAuthRedirectResponse(String location, HttpCookie httpCookie) {
        StringBuilder response = new StringBuilder();
        response.append(HttpStatus.FOUND.getStatusLine()).append(HTTP_LINE_SEPARATOR);
        response.append(HEADER_LOCATION).append(location).append(HTTP_LINE_SEPARATOR);
        if (!httpCookie.hasJSESSIONID()) {
            response.append("Set-Cookie: JSESSIONID=").append(UUID.randomUUID()).append(HTTP_LINE_SEPARATOR);
        }
        response.append(HEADER_CONTENT_LENGTH).append("0").append(HTTP_LINE_SEPARATOR);
        response.append("").append(HTTP_LINE_SEPARATOR);
        response.append("");

        return response.toString();
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
