package org.apache.coyote.http11.controller;

import com.techcourse.model.User;
import com.techcourse.model.UserInfo;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.UserService;

public class UserController implements Controller {

    private final UserService userService = UserService.getInstance();

    @Override
    public boolean canHandle(String url) {
        return url.contains("register");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isMethod(HttpMethod.POST)) {
            UserInfo userInfo = UserInfo.read(httpRequest.getRequestBody());
            User user = new User(userInfo);
            userService.save(user);

            httpResponse.statusCode(HttpStatusCode.FOUND_302)
                    .location("/index.html");
            return;
        }

        httpResponse.statusCode(HttpStatusCode.OK_200)
                .viewUrl("/register.html");
    }
}
