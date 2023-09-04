package org.apache.coyote.http11.handler;

import static java.util.UUID.randomUUID;
import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;
import static nextstep.jwp.db.InMemoryUserRepository.save;
import static org.apache.coyote.header.HttpMethod.POST;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.header.HttpCookie;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.session.Session;
import org.apache.coyote.util.RequestExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostHttp11MethodHandler implements Http11MethodHandler {

    private static final Logger log = LoggerFactory.getLogger(PostHttp11MethodHandler.class);
    private static final String ERROR_PAGE_401 = String.join("\r\n",
            "HTTP/1.1 302 Found",
            "Location: " + "401.html");

    @Override
    public HttpMethod supportMethod() {
        return POST;
    }

    @Override
    public String handle(final String headers, final String payload) {
        String targetPath = RequestExtractor.extractTargetPath(headers);
        Map<String, String> requestBody = toMap(payload);

        if (targetPath.contains("login")) {
            return login(headers, requestBody);
        }
        if (targetPath.contains("register")) {
            return register(requestBody);
        }
        return String.join("\r\n",
                "HTTP/1.1 404 Not Found");
    }

    private Map<String, String> toMap(final String payload) {
        Map<String, String> result = new HashMap<>();
        String[] params = payload.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private String register(final Map<String, String> requestBody) {
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        save(user);

        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + "index.html");
    }

    private String login(final String headers, final Map<String, String> requestBody) {
        User user;
        try {
            user = getUser(headers, requestBody);
        } catch (RuntimeException e) {
            return ERROR_PAGE_401;
        }
        return processLogin(user, requestBody.get("password"));
    }

    private User getUser(final String headers, final Map<String, String> requestBody) {
        HttpCookie httpCookie = HttpCookie.from(headers);
        String sessionId = httpCookie.getValue("JSESSIONID");
        if (sessionId == null) {
            return findByAccount(requestBody.get("account")).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 유저입니다."));
        }
        Session session = SessionManager.findSession(sessionId);
        String account = session.getAttribute("JSESSIONID");
        return findByAccount(account).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    }

    private String processLogin(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info("로그인 성공! -> {}", user);

            Session session = new Session(randomUUID().toString());
            session.setAttribute("JSESSIONID", user.getAccount());
            SessionManager.add(session);

            return String.join("\r\n",
                    "HTTP/1.1 302 Found",
                    "Location: index.html",
                    "Set-Cookie: " + "JSESSIONID=" + session.id());
        }
        return ERROR_PAGE_401;
    }
}
