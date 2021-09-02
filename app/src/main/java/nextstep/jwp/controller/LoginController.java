package nextstep.jwp.controller;


import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.model.httpmessage.session.HttpCookie;
import nextstep.jwp.model.httpmessage.session.HttpSession;
import nextstep.jwp.model.httpmessage.session.HttpSessions;
import nextstep.jwp.view.ModelAndView;

import java.util.Optional;

import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response, ModelAndView mv) {
        Optional<User> findUser = userService.findByAccountAndPassword(request);
        if (findUser.isPresent()) {
            response.setStatus(OK);
            response.setContentType(HTML.value());
            mv.setViewName("/index.html");

            HttpSession session = new HttpSession();
            HttpSessions.addSession(session);
            response.addCookie(new HttpCookie(session));
            session.setAttribute("user", findUser.get());
            return;
        }

        response.setStatus(REDIRECT);
        mv.setViewName("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) {
        if (request.hasSessionId() && request.isValidSession()) {
            response.setStatus(REDIRECT);
            mv.setViewName("/index.html");
            return;
        }

        response.setStatus(OK);
        response.setContentType(HTML.value());
        mv.setViewName("/login.html");
    }
}

