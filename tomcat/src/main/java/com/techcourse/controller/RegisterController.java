package com.techcourse.controller;

import org.apache.catalina.servlet.AbstractController;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpRequestParameters;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StaticResource;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestParameters methodRequest = HttpRequestParameters.parseFrom(request.getBody());
        User user = register(methodRequest);
        Session session = request.getSession(true);
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);

        response.addCookie("JSESSIONID", session.getId());
        response.setMethodFound("/index.html");
    }

    private User register(HttpRequestParameters requestParams) {
        String account = requestParams.getParam("account");
        User user = new User(
                account,
                requestParams.getParam("password"),
                requestParams.getParam("email")
        );
        InMemoryUserRepository.save(user);
        return InMemoryUserRepository.fetchByAccount(account);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponseOfStaticResource(new StaticResource("/register.html"));
    }
}
