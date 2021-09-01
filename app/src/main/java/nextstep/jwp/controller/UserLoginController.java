package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.UserRequest;
import nextstep.jwp.controller.modelview.ModelView;
import nextstep.jwp.httpmessage.HttpRequest;
import nextstep.jwp.httpmessage.HttpResponse;
import nextstep.jwp.service.UserService;

public class UserLoginController extends AbstractController {

    private final UserService userService;

    public UserLoginController() {
        this.userService = new UserService();
    }

    @Override
    protected ModelView doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        return new ModelView("/login.html");
    }

    @Override
    protected ModelView doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        userService.login(new UserRequest(httpRequest.getParameter("account"),
                httpRequest.getParameter("password")));
        return new ModelView("/index.html");
    }
}
