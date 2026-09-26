package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.FormBodyParser;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.resource.StaticResource;
import com.techcourse.resource.StaticResourceLoader;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class RegisterController extends AbstractController {

    private final SessionManager sessionManager;
    private final StaticResourceLoader staticResourceLoader;

    public RegisterController(SessionManager sessionManager, StaticResourceLoader staticResourceLoader) {
        this.sessionManager = sessionManager;
        this.staticResourceLoader = staticResourceLoader;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, List<String>> forms = FormBodyParser.parse(request.getBody());

        if (!forms.containsKey("account") || !forms.containsKey("password") || !forms.containsKey("email")) {
            response.redirect("/register.html");
            return;
        }

        User user = new User(forms.get("account").getFirst(), forms.get("password").getFirst(),
                forms.get("email").getFirst());

        InMemoryUserRepository.save(user);

        Session session = sessionManager.getSession(request.getHeaders(), true);
        session.setAttribute("user", user);
        HttpCookie cookie = new HttpCookie(session.getId());

        response.addHeader("Set-Cookie", List.of(cookie.toString()));
        response.redirect("/index.html");
        log.info("회원가입 성공 : {}", user.toString());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResource staticResource = staticResourceLoader.load(request.getRequestLine().getPath());

        response.setHeader("Content-Type", List.of(staticResource.contentType()));
        response.setBody(staticResource.body());
    }
}
