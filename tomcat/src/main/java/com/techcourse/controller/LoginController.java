package com.techcourse.controller;

import java.util.Objects;

import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpRequestParameters;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestParameters requestParams = HttpRequestParameters.parseFrom(request.getBody());
        String account = requestParams.getParam("account");
        String password = requestParams.getParam("password");
        User user = InMemoryUserRepository.fetchByAccount(account);
        if (user.checkPassword(password)) {
            Session session = request.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);

            response.addCookie("JSESSIONID", session.getId());
            response.setMethodFound("/index.html");
            return;
        }

        response.setMethodFound("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.setMethodFound("/index.html");
            return;
        }
        response.setMethodFound("/index.html");
    }



    private boolean isLoggedIn(HttpRequest httpRequest) {
        return Objects.nonNull(httpRequest.getSession(false));
    }
}
