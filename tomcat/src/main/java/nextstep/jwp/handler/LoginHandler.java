package nextstep.jwp.handler;

import java.util.Map;
import java.util.Optional;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;

public class LoginHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public User login(Map<String, String> request) {
        Optional<User> findUser = InMemoryUserRepository.findByAccount(request.get(ACCOUNT));
        User user = findUser.orElseThrow(MemberNotFoundException::new);
        user.checkPassword(request.get(PASSWORD));
        System.out.println(user);
        return user;
    }
}
