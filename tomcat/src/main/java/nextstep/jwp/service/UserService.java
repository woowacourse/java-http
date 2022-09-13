package nextstep.jwp.service;

import nextstep.jwp.model.User;
import nextstep.jwp.repository.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void login(final HttpRequest request) {
        final String userAccount = request.findQueryValue("account");
        final String userPassword = request.findQueryValue("password");

        final User user = InMemoryUserRepository.findByAccount(userAccount)
                .filter(it -> it.checkPassword(userPassword))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));

        log.info("user : {}", user);
    }
}
