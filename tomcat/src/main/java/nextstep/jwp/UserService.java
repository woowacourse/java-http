package nextstep.jwp;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_PASSWORD = "password";

    public void login(final Map<String, String> params) {
        User user = InMemoryUserRepository.findByAccount(params.get(KEY_ACCOUNT))
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(params.get(KEY_PASSWORD))) {
            log.info(user.toString());
            return;
        }
        throw new IllegalArgumentException();
    }
}
