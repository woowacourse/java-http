package org.apache.coyote.http11.controller;

import com.techcourse.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.resolver.RequestBodyResolver;
import org.apache.coyote.http11.request.resolver.UserResolver;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.UserService;

public class UserController implements Controller {

    private final RequestBodyResolver<User> userResolver = new UserResolver();
    private final UserService userService = UserService.getInstance();

    @Override
    public boolean canHandle(String url) {
        return url.contains("register");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isMethod(HttpMethod.POST)) {
            User user = userResolver.resolve(httpRequest.getRequestBody());
            userService.save(user);

            httpResponse.redirect("/index.html");
            return;
        }

        httpResponse.statusCode(StatusCode.OK_200)
                .viewUrl("/register.html");
    }
}
