package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParser;
import org.apache.coyote.http11.controller.AbstractController;

public class RegisterController extends AbstractController {

    private final HttpResourceLoader httpResourceLoader;
    private final QueryParser queryParser;

    public RegisterController(final HttpResourceLoader httpResourceLoader, final QueryParser queryParser) {
        this.httpResourceLoader = httpResourceLoader;
        this.queryParser = queryParser;
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        String requestBody = new String(request.body());
        Map<String, String> queriesFromBody = queryParser.parse(requestBody);

        String account = queriesFromBody.get("account");
        String email = queriesFromBody.get("email");
        String password = queriesFromBody.get("password");

        UserService.register(account, email, password);

        response.redirect("/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        httpResourceLoader.load(request.getPath(), response);
    }
}
