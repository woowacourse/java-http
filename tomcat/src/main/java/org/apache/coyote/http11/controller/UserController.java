package org.apache.coyote.http11.controller;

import com.techcourse.model.User;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.resolver.RequestBodyResolver;
import org.apache.coyote.http11.request.resolver.UserResolver;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.UserService;

public class UserController extends AbstractController {

    private final RequestBodyResolver<User> userResolver = new UserResolver();
    private final UserService userService = UserService.getInstance();

    @Override
    public boolean canHandle(String url) {
        return url.contains("register");
    }

    @Override
    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        saveUser(httpRequest, httpResponse);
    }

    @Override
    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        registerView(httpResponse);
    }

    private void saveUser(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = userResolver.resolve(httpRequest.getRequestBody());
        userService.save(user);
        httpResponse.redirect("/index.html");
    }

    private void registerView(HttpResponse httpResponse) {
        httpResponse.statusCode(StatusCode.OK_200)
                .viewUrl("/register.html");
    }
}
