package org.apache.coyote;

import com.techcourse.Service;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.common.HttpCookie;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpResponseBuilder;
import org.apache.coyote.common.HttpStatus;

public class LoginHandler {

    private final HttpResponseBuilder responseBuilder;
    private final Service service;

    public LoginHandler(HttpResponseBuilder responseBuilder, Service service) {
        this.responseBuilder = responseBuilder;
        this.service = service;
    }

    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (request.getMethod().equals("GET")) {
            return handleGet(request);
        }
        if (request.getMethod().equals("POST")) {
            return handlePost(request);
        }
        return handleUnsupportedMethod(request);
    }

    private HttpResponse handleGet(final HttpRequest request) throws IOException {
        String uri = request.getPath();

        if (uri.contains(".")) {
            final byte[] body = ResourceLoader.get(uri);
            return responseBuilder.build(request, HttpStatus.OK, null, body);
        }
        if (request.getQueryParams() == null && request.getBody() == null) {
            if (request.getHeaders().containsKey("Cookie") && request.getHeader("Cookie").contains("JSESSIONID")) {
                return handleRedirect(request);
            }
            final byte[] body = ResourceLoader.get(uri + ".html");
            return responseBuilder.build(request, HttpStatus.OK, null, body);
        }
        return responseBuilder.build(request, HttpStatus.FORBIDDEN, null, null);
    }

    private HttpResponse handlePost(HttpRequest request) {
        if (request.getBody() == null) {
            return responseBuilder.build(request, HttpStatus.BAD_REQUEST, null, null);
        }

        Map<String, String> body = new HashMap<>();

        for (String keyValue : request.getBody().split("&")) {
            int index = keyValue.indexOf("=");
            String key = keyValue.substring(0, index);
            String value = keyValue.substring(index + 1);
            body.put(key, value);
        }

        try {
            User user = service.getLoggedInUser(body);
            UUID uuid = createSession(user);

            final Map<String, String> headers = new HashMap<>();
            headers.put("Set-Cookie", "JSESSIONID=" + uuid);
            headers.put("Location", "/index.html");

            return responseBuilder.build(request, HttpStatus.FOUND, headers, null);
        } catch (IllegalArgumentException e) {
            final Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/401.html");
            return responseBuilder.build(request, HttpStatus.FOUND, headers, null);
        }
    }

    private HttpResponse handleRedirect(final HttpRequest request) throws IOException {
        HttpCookie cookie = new HttpCookie(request.getHeader("Cookie"));
        String sessionId = cookie.getValue("JSESSIONID");
        if (sessionId == null) {
            final byte[] body = ResourceLoader.get(request.getPath() + ".html");
            return responseBuilder.build(request, HttpStatus.OK, null, body);
        }
        Manager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            final byte[] body = ResourceLoader.get(request.getPath() + ".html");
            return responseBuilder.build(request, HttpStatus.OK, null, body);
        }
        final Map<String, String> headers = new HashMap<>();
        headers.put("Location", "/index.html");
        return responseBuilder.build(request, HttpStatus.FOUND, headers, null);
    }

    private UUID createSession(final User user) {
        UUID uuid = UUID.randomUUID();
        SessionManager sessionManager = SessionManager.getInstance();
        Session loginSession = new Session(uuid.toString());
        loginSession.setAttribute("user", user);
        sessionManager.add(loginSession);
        return uuid;
    }

    private HttpResponse handleUnsupportedMethod(final HttpRequest request) {
        return responseBuilder.build(request, HttpStatus.FORBIDDEN, null, null);
    }
}
