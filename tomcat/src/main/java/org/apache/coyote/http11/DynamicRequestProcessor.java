package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class DynamicRequestProcessor {

    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HEADER_LOCATION = "Location: ";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";
    public static final String INDEX_HTML = "/index.html";

    public static void processDynamic(String httpMethod, String requestUri, String body, HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        if ("POST".equals(httpMethod)) {
            Map<String, String> formData = FormDataParser.parse(body);
            if (requestUri.equals("/register")) {
                handleRegister(formData, httpCookie, outputStream);
            } else if (requestUri.equals("/login")) {
                handleLogin(formData, httpCookie, outputStream);
            }
        } else {
            if (requestUri.equals("/login") || requestUri.equals("/login.html")) {
                handleLoginPageAccess(httpCookie, outputStream);
            } else if (requestUri.equals("/register") || requestUri.equals("/register.html")) {
                handleRegisterPageAccess(httpCookie, outputStream);
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
        String redirectResponse = buildAuthRedirectResponse(INDEX_HTML, sessionId);
        sendResponse(outputStream, redirectResponse);
    }

    private static String buildAuthRedirectResponse(String location, String sessionId) {
        StringBuilder response = new StringBuilder();
        response.append(HttpStatus.FOUND.getStatusLine()).append(HTTP_LINE_SEPARATOR);
        response.append(HEADER_LOCATION).append(location).append(HTTP_LINE_SEPARATOR);

        if (sessionId != null) {
            response.append("Set-Cookie: JSESSIONID=").append(sessionId).append(HTTP_LINE_SEPARATOR);
        }

        response.append(HEADER_CONTENT_LENGTH).append("0").append(HTTP_LINE_SEPARATOR);
        response.append("").append(HTTP_LINE_SEPARATOR);
        response.append("");
        return response.toString();
    }

    private static void send401Page(OutputStream outputStream) throws IOException {
        try (InputStream inputStream = DynamicRequestProcessor.class.getClassLoader().getResourceAsStream("static/401.html")) {
            byte[] responseBody = inputStream.readAllBytes();
            String response = String.join(HTTP_LINE_SEPARATOR,
                    HttpStatus.UNAUTHORIZED.getStatusLine(),
                    HEADER_CONTENT_TYPE + "text/html;charset=utf-8",
                    HEADER_CONTENT_LENGTH + responseBody.length,
                    "",
                    new String(responseBody, StandardCharsets.UTF_8));
            sendResponse(outputStream, response);
        }
    }

    private static String buildRedirectResponse(String location) {
        return String.join(HTTP_LINE_SEPARATOR,
                HttpStatus.FOUND.getStatusLine(),
                HEADER_LOCATION + location,
                HEADER_CONTENT_LENGTH + "0",
                "",
                "");
    }

    private static void handleLoginPageAccess(HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        handleAuthPageAccess("/login.html", httpCookie, outputStream);
    }

    private static void handleRegisterPageAccess(HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        handleAuthPageAccess("/register.html", httpCookie, outputStream);
    }

    private static void handleAuthPageAccess(String pagePath, HttpCookie httpCookie, OutputStream outputStream) throws IOException, URISyntaxException {
        if (isLoggedIn(httpCookie)) {
            String redirectResponse = buildRedirectResponse(INDEX_HTML);
            sendResponse(outputStream, redirectResponse);
        } else {
            StaticResourceProcessor.processStatic(pagePath, outputStream);
        }
    }

    private static boolean isLoggedIn(HttpCookie httpCookie) {
        if (!httpCookie.hasJSESSIONID()) {
            return false;
        }
        String sessionId = httpCookie.getJSESSIONID();
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            return false;
        }
        return session.getAttribute("user") != null;
    }

    private static void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
