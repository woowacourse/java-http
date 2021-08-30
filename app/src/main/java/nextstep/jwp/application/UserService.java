package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

public class UserService {

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public boolean isExistUser(HttpRequest request) {
        return InMemoryUserRepository.existUserByAccountAndPassword(
                request.getParameter(ACCOUNT),
                request.getParameter(PASSWORD));
    }

    public User save(HttpRequest request) throws UserPrincipalNotFoundException {
        User newUser = new User(request.getParameter(ACCOUNT),
                request.getParameter(PASSWORD),
                request.getParameter(EMAIL));
        InMemoryUserRepository.save(newUser);

        User savedUser = findByAccount(request)
                .orElseThrow(() -> new UserPrincipalNotFoundException("해당 유저가 존재하지 않습니다. (account : " + request.getParameter(ACCOUNT) + ")"));
        LOG.debug("Saved user : {}", savedUser);
        return savedUser;
    }

    public Optional<User> findByAccount(HttpRequest request) {
        return InMemoryUserRepository.findByAccount(request.getParameter(ACCOUNT));
    }
}
