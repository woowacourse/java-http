package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

public class MemberService {

    private MemberService() {
    }

    public static void register(String name, String email, String password) {
        User user = new User(name, email, password);
        InMemoryUserRepository.save(user);
    }

    public static User login(String name, String password) {
        return InMemoryUserRepository.findByAccount(name)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(UserNotFoundException::new);
    }
}
