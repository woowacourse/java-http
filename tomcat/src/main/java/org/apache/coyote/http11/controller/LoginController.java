package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Cookie;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatusCode;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        Http11RequestBody body = request.getRequestBody();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(body.get("account"));
        String redirectUri = "/401.html";

        User user;
        if (optionalUser.isPresent() && (user = optionalUser.get()).checkPassword(body.get("password"))) {
            Session session = Session.getInstance(user);
            SessionManager.add(session);
            response.setCookie("JSESSIONID", session.getId());
            redirectUri = "/index.html";
        }

        redirect(response, redirectUri);
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) {
        Session session = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("JSESSIONID")) {
                session = SessionManager.findSession(cookie.getValue());
            }
        }

        if (session == null) {
            request.setUri(request.getUri() + ".html");
            return;
        }
        redirect(response, "/index.html");
    }

    private void redirect(Http11Response response, String uri) {
        response.setStatusCode(HttpStatusCode.FOUND);
        response.addLocation(uri);
    }
}
