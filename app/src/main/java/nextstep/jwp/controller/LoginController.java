package nextstep.jwp.controller;


import nextstep.jwp.application.UserService;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;
import static nextstep.jwp.model.httpmessage.response.ResponseHeaderType.LOCATION;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        if (userService.isExistUser(request)) {
            response.setStatus(OK);
            response.setContentType(HTML.value());
            mv.setViewName("/index.html");
            return;
        }

        response.setStatus(REDIRECT);
        mv.setViewName("/401.html");
        response.addHeader(LOCATION, "/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        response.setStatus(OK);
        response.setContentType(HTML.value());
        mv.setViewName("/login.html");
    }
}

