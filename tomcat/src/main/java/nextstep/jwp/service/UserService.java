package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String USER_ACCOUNT = "account";
    private static final String USER_PASSWORD = "password";
    private static final String USER_EMAIL = "email";

    private UserService() {
    }

    public static void register(final Parameters parameters) {
        final String account = parameters.getParameter(USER_ACCOUNT);
        final String password = parameters.getParameter(USER_PASSWORD);
        final String email = parameters.getParameter(USER_EMAIL);
        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }

    public static User login(final Parameters parameters) {
        final String account = parameters.getParameter(USER_ACCOUNT);
        final String password = parameters.getParameter(USER_PASSWORD);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 정보입니다."));

        if (user.checkPassword(password)) {
            LOGGER.info("user : {}", user);
            return user;
        }

        throw new IllegalArgumentException("잘못된 유저 정보입니다.");
    }
}
