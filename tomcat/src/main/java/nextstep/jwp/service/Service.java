package nextstep.jwp.service;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Service {

    private static final Logger LOG = LoggerFactory.getLogger(Service.class);

    public void login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(password)) {
            LOG.info(user.toString());
        }
    }
}
