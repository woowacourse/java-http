package nextstep.jwp;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void register(final RegisterRequest request) {
        User user = new User(request.getAccount(), request.getPassword(), request.getEmail());

        InMemoryUserRepository.save(user);
    }

    public void login(final LoginRequest request) {
        User user = InMemoryUserRepository.findByAccount(request.getAccount())
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(request.getPassword())) {
            log.info(user.toString());
            return;
        }
        throw new IllegalArgumentException();
    }
}
