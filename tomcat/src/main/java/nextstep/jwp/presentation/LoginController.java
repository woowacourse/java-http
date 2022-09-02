package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController {

    public void login(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if (user.checkPassword(password)) {
            System.out.println(user);
        }
    }
}
