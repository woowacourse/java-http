package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account).orElseThrow(
                IllegalArgumentException::new);
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
    }

    public void save(String account, String password, String email) {
        User user = new User(2L, account, password, email);
        InMemoryUserRepository.save(user);
    }
}
