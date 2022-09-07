package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.FileReader;

public class LoginController implements Controller {

    private static SessionManager sessionManager = new SessionManager();

    @Override
    public HttpResponse doGet(HttpRequest request) {
        if (request.containsSession()) {
            findUserFromCookie(request);
            return HttpResponse.found("/index.html");
        }

        return HttpResponse.ok("/login.html", FileReader.read("/login.html"));
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        if (request.containsSession()) {
            findUserFromCookie(request);
            return HttpResponse.found("/index.html");
        }

        Map<String, String> body = request.getBody();
        String account = body.get("account");
        String password = body.get("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return HttpResponse.found("/401.html");
        }

        System.out.println(user.get());

        Session session = new Session();
        session.setAttribute("user", user.get());
        sessionManager.add(session);

        HttpResponse response = HttpResponse.found("/index.html");
        response.setCookie(HttpCookie.createWithSession(session.getId()));

        return response;
    }

    private void findUserFromCookie(HttpRequest request) {
        Session session = sessionManager.findSession(request.getSessionFromCookie());
        User user = (User)session.getAttribute("user");
        System.out.println(user);
    }
}
