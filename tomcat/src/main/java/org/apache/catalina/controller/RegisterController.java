package org.apache.catalina.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        Map<String, List<String>> parameters = request.getParameters();
        String account = getFirst(parameters, "account");
        String email = getFirst(parameters, "email");
        String password = getFirst(parameters, "password");

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(new User(account, password, email));
            log.info("Register OK - account {}", account);

            Session session = request.getSession(true);
            session.setAttribute("user", new User(account, password, email));
            String setCookie = HttpCookie.buildSetCookieHeader(session.getId());
            response.addSetCookie(setCookie);

            response.redirect("/index.html");
            return;
        }

        response.redirect("/register.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.redirect("/register.html");
    }

    private String getFirst(final Map<String, List<String>> map, final String key) {
        List<String> list = map.get(key);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.getFirst();
    }
}
