package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.FormBodyParser;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.HttpSession;
import com.techcourse.model.User;
import com.techcourse.resource.StaticResource;
import com.techcourse.resource.StaticResourceLoader;
import java.util.List;
import java.util.Map;

public class RegisterController extends AbstractController {

    private final StaticResourceLoader staticResourceLoader;

    public RegisterController(StaticResourceLoader staticResourceLoader) {
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

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

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
