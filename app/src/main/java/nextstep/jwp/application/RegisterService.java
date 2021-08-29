package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public void register(String account, String password, String email) {
        User user = new User(InMemoryUserRepository.getNextId(), account, password, email);
        InMemoryUserRepository.save(user);
        logger.debug("Saved User !!! - {}", user);
    }
}
