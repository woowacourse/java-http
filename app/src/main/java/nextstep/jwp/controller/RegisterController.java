package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.web.model.HttpSession;

import java.util.Objects;

public class RegisterController implements Controller {

    public String registerPage(HttpRequest httpRequest, HttpSession httpSession) {
        User user = getUser(httpSession);
        if (Objects.nonNull(user)) {
            return "redirect: /index.html";
        }
        return "register.html";
    }

    public String register(HttpRequest httpRequest, HttpSession httpSession) {
        String account = httpRequest.getAttribute("account");
        String password = httpRequest.getAttribute("password");
        String email = httpRequest.getAttribute("email");
        User newUser = new User(account, password, email);

        if (InMemoryUserRepository.existsUser(newUser)) {
            return "redirect: /409.html";
        }
        InMemoryUserRepository.save(newUser);

        return "redirect: /index.html";
    }

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
