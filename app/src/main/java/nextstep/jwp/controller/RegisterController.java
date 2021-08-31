package nextstep.jwp.controller;

import nextstep.jwp.request.HttpRequest;

public class RegisterController implements Controller {

    public String registerPage(HttpRequest httpRequest) {
        return "register.html";
    }

    public String register(HttpRequest httpRequest) {
        return "redirect: /index.html";
    }
}
