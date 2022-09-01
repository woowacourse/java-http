package nextstep.jwp;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public static boolean login(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);
        return user.checkPassword(password);
    }

    public static User findByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);
    }
}
