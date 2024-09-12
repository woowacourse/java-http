package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> body = request.getRequestBody().getBody();
        User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
        setSession(user, response);
        response.setResource(request.getRequestLine().getProtocol(), "/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResource(request.getRequestLine().getProtocol(), request.getUrl() + ".html");
    }

    private void setSession(User user, HttpResponse response) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        session.setAttribute(user.getAccount(), user);
        SessionManager.getInstance().add(session);
        response.putHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }
}
