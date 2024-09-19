package com.techcourse.controller;

import static org.apache.coyote.http11.httpmessage.HttpHeaders.JSESSIONID;

import java.util.Objects;
import java.util.Optional;

import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpRequestParameters;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StaticResource;
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

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if(optionalUser.isEmpty()) {
            response.setStatusFound("/401.html");
            return;
        }

        User user = optionalUser.get();
        if (user.checkPassword(password)) {
            Session session = request.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);

            response.addCookie(JSESSIONID, session.getId());
            response.setStatusFound("/index.html");
            return;
        }

        response.setStatusFound("/401.html");
    }


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.setStatusFound("/index.html");
            return;
        }
        response.setResponseOfStaticResource(new StaticResource("/login.html"));
    }

    private boolean isLoggedIn(HttpRequest httpRequest) {
        return Objects.nonNull(httpRequest.getSession(false));
    }
}
