package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.common.RequestMethod;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements Handler {

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        if (request.getRequestMethod() == RequestMethod.GET) {
            Cookie cookie = request.parseCookie();
            String jsessionid = cookie.findByKey("JSESSIONID");

            if (isLoginUser(jsessionid)) {
                return ResponseEntity.redirect("index.html");
            }

            String fileData = FileReader.readFile("/login.html");
            return ResponseEntity.ok(fileData, "/login.html");
        }
        if (request.getRequestMethod() == RequestMethod.POST) {
            Optional<User> userResult = findUser(request);
            if (userResult.isPresent()) {
                User user = userResult.get();
                return loginSuccessResponse(user);
            }
            return loginFailResponse();
        }
        throw new UnsupportedOperationException("get, post만 가능합니다.");
    }

    private boolean isLoginUser(String jsessionid) {
        if (jsessionid == null) {
            return false;
        }
        Session session = SessionManager.findSession(UUID.fromString(jsessionid));
        return session != null;
    }

    private ResponseEntity loginSuccessResponse(User user) {
        ResponseEntity responseEntity = ResponseEntity.redirect("index.html");
        UUID uuid = createSession(user);
        responseEntity.setCookie("JSESSIONID", uuid.toString());
        return responseEntity;
    }

    private static Optional<User> findUser(HttpRequest request) {
        Map<String, String> queryStrings = request.getQueryStrings();
        String account = queryStrings.get("account");
        String password = queryStrings.get("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private UUID createSession(User user) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid);
        session.setAttribute("user", user);
        SessionManager.add(session);
        SessionManager.add(session);
        return uuid;
    }

    private ResponseEntity loginFailResponse() {
        return ResponseEntity.redirect("401.html");
    }
}
