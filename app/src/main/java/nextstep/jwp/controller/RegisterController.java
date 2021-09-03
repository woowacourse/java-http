package nextstep.jwp.controller;

import nextstep.jwp.application.UserService;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.*;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) {
        response.setResponseLine(OK, request.getProtocol());
        mv.setStatus(OK);
        response.setContentType(HTML.value());
        mv.setViewName("/register");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        if (userService.findByAccount(request).isPresent()) {
            response.setResponseLine(UNAUTHORIZED, request.getProtocol());
            mv.setStatus(UNAUTHORIZED);
            mv.setViewName("/401.html");
            return;
        }
        userService.save(request);
        response.setResponseLine(REDIRECT, request.getProtocol());
        mv.setStatus(REDIRECT);
        mv.setViewName("/index.html");
    }
}
