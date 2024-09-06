package com.techcourse.handler;

import java.io.IOException;
import java.util.Map;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginHandler extends AbstractRequestHandler {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (alreadyLoggedIn(request, response)) return;

        response.setStaticResourceResponse("/login.html");
        response.write();
    }

    private static boolean alreadyLoggedIn(HttpRequest request, HttpResponse response) {
        Session session = request.getSession(false);
        boolean sessionAndUserExists = session != null && session.getAttribute("user") != null;

        if (sessionAndUserExists) {
            response.sendRedirect("/index.html");
            response.write();
            return true;
        }
        return false;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password))
                .orElse(null);
        if (user == null) {
            response.sendRedirect("/401.html");
            response.write();
            return;
        }

        setSessionIfAbsent(request, response, user);

        response.sendRedirect("/index.html");
        response.write();
    }

    private void setSessionIfAbsent(HttpRequest request, HttpResponse response, User user) {
        if (request.sessionNotExists()) {
            Session session = request.getSession(true);
            session.setAttribute("user", user);
            response.addCookie(Cookie.session(session.getId()));
        }
    }
}
