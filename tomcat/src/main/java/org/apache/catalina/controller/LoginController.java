package org.apache.catalina.controller;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class LoginController extends AbstractController {

    @Override
    protected String doPost(HttpRequest httpRequest) throws Exception {
        loginPost(outputStream, httpRequest);
    }

    @Override
    protected String doGet(HttpRequest httpRequest) throws Exception {
        loginGet(outputStream, httpRequest);
    }

    private void loginGet(HttpRequest httpRequest) throws IOException {
        if (SessionManager.getInstance().hasUser(httpRequest.getJSessionId())) {
            HttpResponse httpResponse = HttpResponse.from(httpRequest, HttpStatusCode.FOUND)
            HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
            httpResponse.redirect(outputStream, "/index.html");
            return;
        }
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.OK);
        httpResponse.respond(outputStream);
    }

    private void loginPost(HttpRequest httpRequest) throws IOException {
        String account = httpRequest.getRequestParam("account");
        String password = httpRequest.getRequestParam("password");
        if (account.isEmpty() || password.isEmpty()) {
            loginFail(outputStream, httpRequest);
        }
        User user = findByAccount(account).orElse(null);
        if (user != null && user.checkPassword(password)) {
            loginSuccess(outputStream, httpRequest, user);
        }
        if (user != null && !user.checkPassword(password)) {
            loginFail(outputStream, httpRequest);
        }
        if (!account.isEmpty() && user == null) {
            loginFail(outputStream, httpRequest);
        }

    }

    private void loginFail(HttpRequest httpRequest) throws IOException {
        log.info("login fail");
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
        httpResponse.redirect(outputStream, "/401.html");
    }

    private void loginSuccess(HttpRequest httpRequest, User user) throws IOException {
        log.info(user.toString());
        final var session = httpRequest.getSession(true);
        session.setAttribute("user", user);
        HttpResponse httpResponse = handling(httpRequest, HttpStatusCode.FOUND);
        httpResponse.redirect(outputStream, "/index.html");
    }
}
