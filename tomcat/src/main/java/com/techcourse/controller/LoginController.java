package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.util.FileReader;
import org.apache.coyote.util.HttpResponseBuilder;

public final class LoginController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse httpResponse) {
        Cookies cookies = request.getCookies();
        if (cookies.hasJSESSIONID() && SessionManager.isRegistered(cookies.getJSESSIONID())) {
            HttpResponseBuilder.buildRedirection(httpResponse, "/");
            return;
        }
        String fileName = "login.html";
        List<String> contentLines = FileReader.readAllLines(fileName);
        HttpResponseBuilder.buildStaticContent(httpResponse, fileName, contentLines);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
        String account = request.getParams("account");
        String password = request.getParams("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnauthorizedException::new);
        if (user.checkPassword(password)) {
            HttpResponseBuilder.buildRedirection(httpResponse, "/");
            registerLoginSession(request, httpResponse, user);
            return;
        }
        HttpResponseBuilder.buildRedirection(httpResponse, "/401.html");
    }

    private void registerLoginSession(HttpRequest request, HttpResponse httpResponse, User user) {
        Cookies requestCookies = request.getCookies();
        if (!requestCookies.hasJSESSIONID() || SessionManager.isRegistered(requestCookies.getJSESSIONID())) {
            Session loginSession = new Session();
            loginSession.setAttribute("user", user);
            SessionManager.register(loginSession);
            Cookies cookies = new Cookies();
            cookies.setCookie("JSESSIONID", loginSession.getId());
            httpResponse.setCookies(cookies);
        }
    }
}
