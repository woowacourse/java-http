package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.httpMessage.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    public boolean isExistUser(HttpRequest request) {
        return InMemoryUserRepository.existUserByAccountAndPassword(
                request.getParameter(ACCOUNT),
                request.getParameter(PASSWORD));
    }

    public void saveUser(HttpRequest request) {
        User newUser = new User(request.getParameter(ACCOUNT),
                request.getParameter(PASSWORD),
                request.getParameter(EMAIL));
        InMemoryUserRepository.save(newUser);

        Optional<User> savedUser = findByAccount(request);
        LOG.debug("Saved user : {}", savedUser);
    }

    public Optional<User> findByAccount(HttpRequest request) {
        return InMemoryUserRepository.findByAccount(request.getParameter(ACCOUNT));
    }
}
