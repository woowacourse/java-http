package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestData;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.request.RequestMethod.GET;
import static org.apache.coyote.http11.request.RequestMethod.POST;

public class LoginHandler implements Handler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (httpRequest.getRequestMethod() == GET) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.getRequestMethod() == POST) {
            doPost(httpRequest, httpResponse);
            return;
        }
        throw new UnsupportedOperationException("get, post만 가능합니다.");
    }

    private void doPost(HttpRequest request, HttpResponse httpResponse) throws Exception {
        Optional<User> userResult = findUser(request);
        if (userResult.isPresent()) {
            User user = userResult.get();
            loginSuccessResponse(user, httpResponse);
            return;
        }
        loginFailResponse(httpResponse);
    }

    private void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        Cookie cookie = request.parseCookie();
        String jsessionid = cookie.findByKey("JSESSIONID");

        if (isLoginUser(jsessionid)) {
            httpResponse.redirect("index.html");
            return;
        }

        String fileData = FileReader.readFile("/login.html");
        httpResponse.ok(fileData, HTML);
    }

    private boolean isLoginUser(String jsessionid) {
        if (jsessionid == null) {
            return false;
        }
        Session session = SessionManager.findSession(UUID.fromString(jsessionid));
        return session != null;
    }

    private void loginSuccessResponse(User user, HttpResponse httpResponse) {
        httpResponse.redirect("index.html");
        UUID uuid = createSession(user);
        httpResponse.addCookie("JSESSIONID", uuid.toString());
    }

    private static Optional<User> findUser(HttpRequest request) {
        RequestData requestData = request.getRequestData();
        String account = requestData.find("account");
        String password = requestData.find("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private UUID createSession(User user) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid);
        session.setAttribute("user", user);
        SessionManager.add(session);
        return uuid;
    }

    private void loginFailResponse(HttpResponse httpResponse) {
        httpResponse.redirect("401.html");
    }
}
