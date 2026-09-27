package org.apache.catalina.controller;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected String doPost(HttpRequest httpRequest) throws Exception {
        String account = httpRequest.getRequestParam("account");
        String password = httpRequest.getRequestParam("password");
        if (account.isEmpty() || password.isEmpty()) {
            return loginFail(httpRequest);
        }
        User user = findByAccount(account).orElse(null);
        return checkUser(httpRequest, user, password);
    }

    private String checkUser(HttpRequest httpRequest, User user, String password) throws IOException {
        if (user == null) {
            return loginFail(httpRequest);
        }
        if (!user.checkPassword(password)) {
            return loginFail(httpRequest);
        }
        return loginSuccess(httpRequest, user);
    }

    @Override
    protected String doGet(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = HttpResponse.of(httpRequest);
        if (SessionManager.getInstance().hasUser(httpRequest.getJSessionId())) {
            return httpResponse.found("/index.html");
        }
        return httpResponse.ok();
    }

    private String loginFail(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = HttpResponse.of(httpRequest);
        return httpResponse.found("/401.html");
    }

    private String loginSuccess(HttpRequest httpRequest, User user) throws IOException {
        final var session = httpRequest.getSession(true);
        session.setAttribute("user", user);

        String rawCookie = "JSESSIONID=" + session.getId();
        HttpCookie httpCookie = HttpCookie.from(rawCookie);
        HttpResponse httpResponse = HttpResponse.from(httpRequest, httpCookie);
        return httpResponse.found("/index.html");
    }
}
