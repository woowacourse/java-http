package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExistingUserException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);

    public void register(String account, String password, String email) {
        if (
            InMemoryUserRepository.findByAccount(account)
                .isPresent()
        ) {
            logger.error("Failed to register new user! account: {}", account);
            throw new ExistingUserException();
        }

        User user = new User(InMemoryUserRepository.getNextId(), account, password, email);
        InMemoryUserRepository.save(user);
        logger.debug("Saved User !!! - {}", user);
    }
}
