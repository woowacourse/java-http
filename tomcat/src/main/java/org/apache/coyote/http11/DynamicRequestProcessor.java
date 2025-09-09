package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DynamicRequestProcessor {

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_LOCATION = "Location";
    public static final String INDEX_HTML = "/index.html";

    public static void process(HttpRequest request, OutputStream outputStream) throws IOException, URISyntaxException {
        if (request.isPost()) {
            handlePostRequest(request, outputStream);
        } else {
            handleGetRequest(request, outputStream);
        }
    }

    private static void handlePostRequest(HttpRequest request, OutputStream outputStream) throws IOException, URISyntaxException {
        String requestUri = request.getRequestUri();
        Map<String, String> formData = FormDataParser.parse(request.getBody());
        if (requestUri.equals("/register")) {
            handleRegister(formData, request.getHttpCookie(), outputStream);
        } else if (requestUri.equals("/login")) {
            handleLogin(formData, request.getHttpCookie(), outputStream);
        }
    }

    private static void handleGetRequest(HttpRequest request, OutputStream outputStream) throws IOException, URISyntaxException {
        String requestUri = request.getRequestUri();
        if (requestUri.equals("/login") ||  requestUri.equals("/register")) {
            handleAuthPageAccess(requestUri, request.getHttpCookie(), outputStream);
        } else {
            String resourcePath = requestUri.equals("/") ? INDEX_HTML : requestUri;
            String requestLine = "GET " + resourcePath + " HTTP/1.1";
            HttpRequest staticRequest = HttpRequest.from(requestLine, new HashMap<>(), "");
            StaticResourceProcessor.process(staticRequest, outputStream);
        }
    }

    private static void handleRegister(Map<String, String> formData, HttpCookie httpCookie, OutputStream outputStream) throws IOException {
        String sessionId = AuthHandler.register(formData, httpCookie);
        handleAuthSuccess(sessionId, outputStream);
    }

    private static void handleLogin(Map<String, String> formData, HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        try {
            String sessionId = AuthHandler.authenticate(formData, httpCookie);
            handleAuthSuccess(sessionId, outputStream);
        } catch (IllegalArgumentException e) {
            send401Page(outputStream);
        }
    }

    private static void handleAuthSuccess(String sessionId, OutputStream outputStream) throws IOException {
        HttpResponse redirectResponse = buildAuthRedirectResponse(INDEX_HTML, sessionId);
        sendResponse(outputStream, redirectResponse);
    }

    private static HttpResponse buildAuthRedirectResponse(String location, String sessionId) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_LOCATION, location);
        headers.put(HEADER_CONTENT_LENGTH, "0");
        
        if (sessionId != null) {
            headers.put("Set-Cookie", "JSESSIONID=" + sessionId);
        }
        return new HttpResponse("HTTP/1.1", HttpStatus.FOUND, headers, "");
    }

    private static void send401Page(OutputStream outputStream) throws IOException {
        try (InputStream inputStream = DynamicRequestProcessor.class.getClassLoader().getResourceAsStream("static/401.html")) {
            byte[] responseBody = inputStream.readAllBytes();
            Map<String, String> headers = new HashMap<>();
            headers.put(HEADER_CONTENT_TYPE, "text/html;charset=utf-8");
            headers.put(HEADER_CONTENT_LENGTH, String.valueOf(responseBody.length));
            
            HttpResponse response = new HttpResponse(
                    "HTTP/1.1",
                    HttpStatus.UNAUTHORIZED,
                    headers,
                    new String(responseBody, StandardCharsets.UTF_8)
            );
            sendResponse(outputStream, response);
        }
    }

    private static HttpResponse buildRedirectResponse(String location) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_LOCATION, location);
        headers.put(HEADER_CONTENT_LENGTH, "0");
        return new HttpResponse("HTTP/1.1", HttpStatus.FOUND, headers, "");
    }

    private static void handleAuthPageAccess(String pagePath, HttpCookie httpCookie, OutputStream outputStream) throws IOException {
        if (AuthHandler.isLoggedIn(httpCookie)) {
            HttpResponse redirectResponse = buildRedirectResponse(INDEX_HTML);
            sendResponse(outputStream, redirectResponse);
        } else {
            String requestLine = "GET " + pagePath + " HTTP/1.1";
            HttpRequest request = HttpRequest.from(requestLine, new HashMap<>(), "");
            StaticResourceProcessor.process(request, outputStream);
        }
    }

    private static void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toHttpString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
