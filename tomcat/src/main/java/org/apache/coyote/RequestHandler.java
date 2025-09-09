package org.apache.coyote;

import com.techcourse.Service;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class RequestHandler {

    private final ResponseBuilder responseBuilder;
    private final Service service;
    private final List<String> viewPaths = List.of("/login", "/register");

    public RequestHandler() {
        this.responseBuilder = new ResponseBuilder();
        this.service = new Service();
    }

    public String handle(final HttpRequest request) throws IOException {
        String uri = request.uri();
        String path = uri.substring(1);

        if (request.method().equals("GET")) {
            return handleGet(request);
        }

        if (request.method().equals("POST")) {
            return handlePost(path, request.body());
        }

        return null;
    }

    private String handleGet(final HttpRequest request) throws IOException {
        String uri = request.uri();

        if (uri.contains(".")) {
            final byte[] body = ResourceLoader.get(uri);
            return responseBuilder.build(uri, "200 OK", body, null);
        }
        if (viewPaths.contains(uri) && request.queryParams() == null && request.body() == null) {
            final byte[] body = ResourceLoader.get(uri + ".html");
            return responseBuilder.build(uri + ".html", "200 OK", body, null);
        }
        return responseBuilder.build("", "403 Forbidden", null, null);
    }

    private String handlePost(final String path, final String body) {
        if (path.startsWith("login")) {
            Map<String, String> map = new HashMap<>();
            for (String keyValue : body.split("&")) {
                int index = keyValue.indexOf("=");
                String key = keyValue.substring(0, index);
                String value = keyValue.substring(index + 1);
                map.put(key, value);
            }

            User user;
            try {
                user = service.findUser(map);
            } catch (IllegalArgumentException e) {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Location", "/401.html");
                return responseBuilder.build(null, "302 Found", new byte[0], headers);
            }

            final Map<String, String> headers = new HashMap<>();
            UUID uuid = UUID.randomUUID();
            headers.put("Set-Cookie", "JSESSIONID=" + uuid);
            headers.put("Location", "/index.html");

            SessionManager sessionManager = SessionManager.getInstance();
            Session loginSession = new Session(uuid.toString());
            loginSession.setAttribute("user", user);
            sessionManager.add(new Session(uuid.toString()));

            return responseBuilder.build(null, "302 Found", null, headers);
        }

        if (path.startsWith("register")) {
            Map<String, String> map = new HashMap<>();
            for (String keyValue : body.split("&")) {
                int index = keyValue.indexOf("=");
                String key = keyValue.substring(0, index);
                String value = keyValue.substring(index + 1);
                map.put(key, value);
            }
            User user = service.registerUser(map.get("account"), map.get("password"), map.get("email"));
            final Map<String, String> headers = new HashMap<>();
            UUID uuid = UUID.randomUUID();
            headers.put("Set-Cookie", "JSESSIONID=" + uuid);
            headers.put("Location", "/index.html");

            SessionManager sessionManager = SessionManager.getInstance();
            Session loginSession = new Session(uuid.toString());
            loginSession.setAttribute("user", user);
            sessionManager.add(new Session(uuid.toString()));

            return responseBuilder.build(null, "302 Found", null, headers);
        }

        return responseBuilder.build(path, "", null, null);
    }
}
