package com.techcourse.auth;

import com.techcourse.auth.session.Session;
import com.techcourse.auth.session.SessionManager;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.user.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.TextResource;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.header.Location;
import org.apache.coyote.http11.response.header.SetCookie;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoginHandler extends HttpRequestHandler {

    private static final String JSESSIONID = "JSESSIONID";
    private final SessionManager sessionManager = new SessionManager();

    @Override
    public String getSupportedUrl() {
        return "/login";
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        if (isUserLoggedIn(request)) {
            return HttpResponse.http11Builder(HttpStatus.FOUND)
                    .header(new Location("/index.html"))
                    .build();
        }
        ResponseBody responseBody = ResponseBody.fromTextResource(getTextResource("/login.html"));
        return HttpResponse.http11Builder(HttpStatus.OK)
                .body(responseBody)
                .build();
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        Map<String, String> body = parserBody(request);
        String account = body.get("account");
        String password = body.get("password");

        if (account == null || password == null) {
            ResponseBody responseBody = ResponseBody.fromTextResource(getTextResource("/login.html"));
            return HttpResponse.http11Builder(HttpStatus.OK)
                    .body(responseBody)
                    .build();
        }

        Optional<User> user = InMemoryUserRepository.findByAccountAndPassword(account, password);
        if (user.isPresent()) {
            Session session = Session.fromUser(user.get());
            sessionManager.add(session);
            return HttpResponse.http11Builder(HttpStatus.FOUND)
                    .headers(List.of(new Location("/index.html"), createSessionCookie(session)))
                    .build();
        }

        ResponseBody responseBody = ResponseBody.fromTextResource(getTextResource("/401.html"));
        return HttpResponse.http11Builder(HttpStatus.OK)
                .body(responseBody)
                .build();
    }

    public Map<String, String> parserBody(HttpRequest request) {
        String urlEncodedBody = request.body();
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

    private boolean isUserLoggedIn(HttpRequest request) {
        String sessionId = request.getCookie(JSESSIONID);
        if (sessionId == null) {
            return false;
        }
        long userId = sessionManager.find(sessionId).getUserId();
        return InMemoryUserRepository.existsById(userId);
    }

    private SetCookie createSessionCookie(Session session) {
        return new SetCookie.Builder(JSESSIONID, session.getId())
                .httpOnly()
                .path("/")
                .maxAge(3600)
                .sameSite(SetCookie.SameSite.LAX)
                .build();
    }

    private TextResource getTextResource(String path) {
        try {
            return TextResource.fromPath("static" + path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
