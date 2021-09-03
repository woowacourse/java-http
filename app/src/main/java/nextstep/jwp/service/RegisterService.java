package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterService.class);

    public void save(User user) {
        InMemoryUserRepository.save(user);
        LOG.info("{} user create success", user.getAccount());
    }
}
