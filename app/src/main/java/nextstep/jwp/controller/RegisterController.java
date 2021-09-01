package nextstep.jwp.controller;

import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        response.setStatus(OK);
        response.setContentType(HTML.value());
        mv.setViewName("/register");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        if (userService.findByAccount(request).isPresent()) {
            response.setStatus(REDIRECT);
            mv.setViewName("/401.html");
            return;
        }
        User savedUser = userService.save(request); // TODO : 로그인된 유저의 데이터를 활용
        response.setStatus(REDIRECT);
        mv.setViewName("/index.html");
    }
}
