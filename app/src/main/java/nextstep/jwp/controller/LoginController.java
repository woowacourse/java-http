package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;

public class LoginController implements Controller {

    public String loginPage(HttpRequest httpRequest) {
        return "login.html";
    }

    public String login(HttpRequest httpRequest) {
        String account = httpRequest.getAttribute("account");
        String password = httpRequest.getAttribute("password");
        User findUser = InMemoryUserRepository.findByAccount(account).orElseThrow(MemberNotFoundException::new);
        if (findUser.invalidPassword(password)) {
            return "redirect: /401.html";
        }
        return "redirect: /index.html";
    }
}
