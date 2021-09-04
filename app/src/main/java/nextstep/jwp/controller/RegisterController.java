package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseStatus;
import nextstep.jwp.service.UserService;

public class RegisterController extends AbstractController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.status(HttpResponseStatus.OK);
        httpResponse.resource("/register.html");
    }


    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

        userService.save(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"),
                httpRequest.getQueryValue("email"));

        httpResponse.status(HttpResponseStatus.FOUND);
        httpResponse.location("/index.html");
    }
}
