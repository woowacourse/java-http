package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.util.Optional;

public class MemberService {

    public static void register(String name, String email, String password) {
        User user = new User(name, email, password);
        InMemoryUserRepository.save(user);
    }

    public static Optional<User> login(String name, String password) {
        return InMemoryUserRepository.findByAccount(name)
                .filter(it -> it.checkPassword(password));
    }
}
