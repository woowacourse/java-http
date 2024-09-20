package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class GreetingController extends HttpController {

    public GreetingController(String path) {
        super(path);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String id = request.getCookieValue(Session.SESSION_KEY);
        Session session = SessionManager.findSession(id);

        User user = (User)session.getAttribute("user");

        response.setContentType("text/plain;charset=utf-8");
        response.setBody(String.join("\r\n",
                String.format("HELLO <%s>, WELCOME!",user.getAccount()),
                String.format("YOUR EMAIL IS <%s>",user.getEmail()),
                "NICE TO SEE YOU ^_^"));
    }
}
