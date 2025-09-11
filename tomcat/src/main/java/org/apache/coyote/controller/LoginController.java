package org.apache.coyote.controller;

import com.techcourse.Service;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.common.HttpCookie;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.MediaTypes;
import org.apache.coyote.util.ResourceLoader;

public class LoginController extends AbstractController {

    private final Service service = new Service();

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        String method = request.getMethod();

        if (Objects.equals(method, "GET")) {
            handleRedirect(request, response);
            return;
        }
        if (Objects.equals(method, "POST")) {
            doPost(request, response);
            return;
        }
        handleUnsupportedMethod(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String path = request.getPath();
        byte[] body;

        if (path.contains(".")) {
            body = ResourceLoader.get(path);
            response.setHeader("Content-Type", MediaTypes.findMediaType(path));
        } else {
            body = ResourceLoader.get(path + ".html");
            response.setHeader("Content-Type", MediaTypes.findMediaType(path + ".html"));
        }

        if (body == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
        } else {
            response.setStatus(HttpStatus.OK);
            response.setBody(body);
        }

        response.setProtocol(request.getProtocol());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.setProtocol(request.getProtocol());
        if (request.getBody() == null) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            return;
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

            response.setStatus(HttpStatus.FOUND);
            response.setHeaders(headers);
        } catch (IllegalArgumentException e) {
            final Map<String, String> headers = new HashMap<>();
            headers.put("Location", "/401.html");

            response.setStatus(HttpStatus.FOUND);
            response.setHeaders(headers);
        }
    }

    private void handleRedirect(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpCookie cookie = request.getCookie();
        String sessionId = cookie.getValue("JSESSIONID");

        if (sessionId == null) {
            doGet(request, response);
            return;
        }

        Manager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            doGet(request, response);
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", "/index.html");
        response.setHeaders(headers);
        response.setStatus(HttpStatus.FOUND);
        response.setProtocol(request.getProtocol());
    }

    private UUID createSession(final User user) {
        UUID uuid = UUID.randomUUID();
        SessionManager sessionManager = SessionManager.getInstance();
        Session loginSession = new Session(uuid.toString());
        loginSession.setAttribute("user", user);
        sessionManager.add(loginSession);
        return uuid;
    }
}
