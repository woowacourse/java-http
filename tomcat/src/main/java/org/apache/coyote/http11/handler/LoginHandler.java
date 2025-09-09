package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler extends HttpRequestHandler {

    private final SessionManager sessionManager = new SessionManager();

    @Override
    String getSupportedUrl() {
        return "/login";
    }

    @Override
    protected HttpResponse handleGet(String request) {
        String jSessionId = getCookie(request, "JSESSIONID");
        if (jSessionId != null) {
            Session session = sessionManager.find(jSessionId);
            long userId = Long.parseLong((String) session.getAttribute("userId"));
            if (InMemoryUserRepository.existsById(userId)) {
                return new HttpResponse(
                        HttpStatus.FOUND,
                        "",
                        MimeType.ANY,
                        Map.of("Location", "/index.html")
                );
            }
        }
        Resource responseBody = getResource("/login.html");
        MimeType mimeType = MimeType.fromResource(responseBody);
        return new HttpResponse(HttpStatus.OK, responseBody.content(), mimeType, Map.of());
    }

    @Override
    protected HttpResponse handlePost(String request) {
        String jSessionId = getCookie(request, "JSESSIONID");
        if (jSessionId != null) {
            Session session = sessionManager.find(jSessionId);
            long userId = Long.parseLong((String) session.getAttribute("userId"));
            if (InMemoryUserRepository.existsById(userId)) {
                return new HttpResponse(
                        HttpStatus.FOUND,
                        "",
                        MimeType.ANY,
                        Map.of("Location", "/index.html")
                );
            }
        }
        Map<String, String> body = parserBody(request);
        String account = body.get("account");
        String password = body.get("password");
        if (account == null | password == null) {
            Resource responseBody = getResource("/login.html");
            MimeType mimeType = MimeType.fromResource(responseBody);
            return new HttpResponse(HttpStatus.OK, responseBody.content(), mimeType, Map.of());
        }

        Optional<User> user = InMemoryUserRepository.findByAccountAndPassword(account, password);
        if (user.isPresent()) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("userId", String.valueOf(user.get().getId()));
            sessionManager.add(session);
            return new HttpResponse(
                    HttpStatus.FOUND,
                    "",
                    MimeType.ANY,
                    Map.of(
                            "Location", "/index.html",
                            "Set-Cookie", "JSESSIONID=" + session.getId()
                    )
            );
        }

        Resource responseBody = getResource("/401.html");
        MimeType mimeType = MimeType.fromResource(responseBody);
        return new HttpResponse(HttpStatus.OK, responseBody.content(), mimeType, Map.of());
    }

    public Map<String, String> parserBody(String request) {
        String urlEncodedBody = request.split("\\n\\n")[1];
        Map<String, String> result = new HashMap<>();
        String[] pairs = urlEncodedBody.split("&");

        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx == -1) {
                continue;
            }
            String key = pair.substring(0, idx);
            String value = pair.substring(idx + 1);
            String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
            String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
            result.put(decodedKey, decodedValue);
        }

        return result;
    }

    private String getCookie(String request, String key) {
        String[] headers = request.split("\\n");
        for (String header : headers) {
            if (header.startsWith("Cookie:")) {
                String[] cookies = header.substring(7).split(";");
                for (String cookie : cookies) {
                    String[] cookiePair = cookie.trim().split("=");
                    if (cookiePair.length == 2 && cookiePair[0].equals(key)) {
                        return cookiePair[1];
                    }
                }
            }
        }
        return null;
    }

    private Resource getResource(String path) {
        try {
            return Resource.fromPath("static" + path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
