package org.apache.coyote.http11.service;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static User findUser(final String account) {
        final String errorMessage = account + "가 없습니다.";

        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> {
                    log.info(errorMessage);
                    throw new NoSuchElementException(errorMessage);
                });
    }

    public static User findUser(final Session session) {
        if (session.containsAttribute("user")) {
            return (User) session.getAttribute("user");
        }
        throw new IllegalArgumentException("user가 없습니다.");
    }
}
