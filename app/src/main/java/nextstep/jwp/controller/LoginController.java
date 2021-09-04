package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.web.model.HttpSession;

import java.util.Objects;

public class LoginController implements Controller {

    public String loginPage(HttpRequest httpRequest, HttpSession httpSession) {
        User user = getUser(httpSession);
        if (Objects.nonNull(user)) {
            return "redirect: /index.html";
        }
        return "login.html";
    }

    public String login(HttpRequest httpRequest, HttpSession httpSession) {
        String account = httpRequest.getAttribute("account");
        String password = httpRequest.getAttribute("password");
        User findUser = InMemoryUserRepository.findByAccount(account).orElseThrow(MemberNotFoundException::new);
        if (findUser.invalidPassword(password)) {
            return "redirect: /401.html";
        }
        httpSession.setAttribute("user", findUser);
        return "redirect: /index.html";
    }

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
