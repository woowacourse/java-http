package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;

public class RegisterController implements Controller {

    public String registerPage(HttpRequest httpRequest) {
        return "register.html";
    }

    public String register(HttpRequest httpRequest) {
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
}
