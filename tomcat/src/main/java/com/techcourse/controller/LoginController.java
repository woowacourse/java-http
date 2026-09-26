package com.techcourse.controller;

import com.techcourse.FormBodyParser;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.HttpSession;
import com.techcourse.resource.StaticResource;
import com.techcourse.resource.StaticResourceLoader;
import java.util.List;
import java.util.Map;

public class LoginController extends AbstractController {
    private final StaticResourceLoader resourceLoader;

    public LoginController(StaticResourceLoader staticResourceLoader) {
        this.resourceLoader = staticResourceLoader;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, List<String>> forms = FormBodyParser.parse(request.getBody());

        if (!forms.containsKey("account") || !forms.containsKey("password")) {
            response.redirect("/login.html");
            return;
        }

        String account = forms.get("account").getFirst();
        String password = forms.get("password").getFirst();

        final var user = InMemoryUserRepository
                .findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password))
                .orElse(null);

        if (user == null) {
            response.redirect("/401.html");
            return;
        }

        // 성공한 경우에만 세션 생성
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        response.redirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            response.redirect("/index.html");
            return;
        }

        StaticResource resource = resourceLoader.load(request.getRequestLine().getPath());
        response.setHeader("Content-Type", List.of(resource.contentType()));
        response.setBody(resource.body());
    }
}
