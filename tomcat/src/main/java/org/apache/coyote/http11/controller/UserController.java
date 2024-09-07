package org.apache.coyote.http11.controller;

import com.techcourse.model.User;
import com.techcourse.model.UserInfo;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBuilder;
import org.apache.coyote.http11.service.UserService;

public class UserController implements Controller {

    private final UserService userService = new UserService();

    @Override
    public boolean canHandle(String url) {
        return url.contains("register");
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.isMethod(HttpMethod.POST)) {
            UserInfo userInfo = UserInfo.read(httpRequest.getRequestBody());
            User user = new User(userInfo);
            userService.save(user);

            return new ResponseBuilder()
                    .statusCode(302)
                    .location("/index.html")
                    .build();
        }

        return new ResponseBuilder()
                .statusCode(200)
                .viewUrl("/register.html")
                .build();
    }
}
