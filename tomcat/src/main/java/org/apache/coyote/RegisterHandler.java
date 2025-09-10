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

public class RegisterHandler {

    private final ResponseBuilder responseBuilder;
    private final Service service;

    public RegisterHandler(ResponseBuilder responseBuilder, Service service) {
        this.responseBuilder = responseBuilder;
        this.service = service;
    }

    public byte[] handle(final HttpRequest request) throws IOException {
        if (request.method().equals("GET")) {
            return handleGet(request);
        }
        if (request.method().equals("POST")) {
            return handlePost(request);
        }
        return handleUnsupportedMethod();
    }

    private byte[] handleGet(final HttpRequest request) throws IOException {
        String uri = request.uri();

        if (uri.contains(".")) {
            final byte[] body = ResourceLoader.get(uri);
            return responseBuilder.build(uri, HttpStatus.OK, body, null);
        }
        if (request.queryParams() == null && request.body() == null) {
            if (request.headers().containsKey("Cookie") && request.headers().get("Cookie").contains("JSESSIONID")) {
                return handleRedirect(request);
            }
            final byte[] body = ResourceLoader.get(uri + ".html");
            return responseBuilder.build(uri + ".html", HttpStatus.OK, body, null);
        }
        return responseBuilder.build(null, HttpStatus.FORBIDDEN, null, null);
    }

    private byte[] handlePost(final HttpRequest request) {
        if (request.body() == null) {
            return responseBuilder.build(null, HttpStatus.BAD_REQUEST, null, null);
        }

        Map<String, String> body = new HashMap<>();

        for (String keyValue : request.body().split("&")) {
            int index = keyValue.indexOf("=");
            String key = keyValue.substring(0, index);
            String value = keyValue.substring(index + 1);
            body.put(key, value);
        }

        User user = service.registerUser(body.get("account"), body.get("password"), body.get("email"));
        UUID uuid = createSession(user);

        final Map<String, String> headers = new HashMap<>();
        headers.put("Set-Cookie", "JSESSIONID=" + uuid);
        headers.put("Location", "/index.html");

        return responseBuilder.build(null, HttpStatus.FOUND, null, headers);
    }

    private byte[] handleRedirect(final HttpRequest request) throws IOException {
        HttpCookie cookie = new HttpCookie(request.headers().get("Cookie"));
        String sessionId = cookie.getValue("JSESSIONID");
        if (sessionId == null) {
            final byte[] body = ResourceLoader.get(request.uri() + ".html");
            return responseBuilder.build(request.uri() + ".html", HttpStatus.OK, body, null);
        }
        Manager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            final byte[] body = ResourceLoader.get(request.uri() + ".html");
            return responseBuilder.build(request.uri() + ".html", HttpStatus.OK, body, null);
        }
        final Map<String, String> headers = new HashMap<>();
        headers.put("Location", "/index.html");
        return responseBuilder.build(null, HttpStatus.FOUND, null, headers);
    }

    private UUID createSession(final User user) {
        UUID uuid = UUID.randomUUID();
        SessionManager sessionManager = SessionManager.getInstance();
        Session loginSession = new Session(uuid.toString());
        loginSession.setAttribute("user", user);
        sessionManager.add(loginSession);
        return uuid;
    }

    private byte[] handleUnsupportedMethod() {
        return responseBuilder.build(null, HttpStatus.FORBIDDEN, null, null);
    }
}
