package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.httpMessage.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public boolean isExistUser(HttpRequest request) {
        return InMemoryUserRepository.existUserByAccountAndPassword(
                request.getParameter("account"),
                request.getParameter("password"));
    }

    public void saveUser(HttpRequest request) {
        User newUser = new User(request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email"));
        InMemoryUserRepository.save(newUser);

        Optional<User> savedUser = findByAccount(request);
        LOG.debug("Saved user : {}", savedUser);
    }

    public Optional<User> findByAccount(HttpRequest request) {
        return InMemoryUserRepository.findByAccount(request.getParameter("account"));
    }
}
