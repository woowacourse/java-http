package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    public LoginHandler() {
    }

    public boolean login(final String requestBody) {
        final QueryParams queryParams = QueryParams.from(requestBody);
        final String account = queryParams.getValueFromKey("account");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return false;
        }

        final String userInformation = user.get().toString();
        log.info(userInformation);
        return user.get().checkPassword(queryParams.getValueFromKey("password"));
    }

    public User getUser(final String requestBody) {
        final QueryParams queryParams = QueryParams.from(requestBody);
        final String account = queryParams.getValueFromKey("account");

        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }
}
