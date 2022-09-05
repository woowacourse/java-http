package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.model.User;
import nextstep.jwp.repository.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public void login(final HttpRequest request) {
        final Map<String, String> bodyParams = request.parseApplicationFormData();
        final String userAccount = bodyParams.get("account");
        final String userPassword = bodyParams.get("password");

        final User user = InMemoryUserRepository.findByAccount(userAccount)
                .filter(it -> it.checkPassword(userPassword))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));

        log.info("user : {}", user);
    }

    public void register(final HttpRequest request) {
        final Map<String, String> bodyParams = request.parseApplicationFormData();
        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");
        final String email = bodyParams.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("saved user : {}", user);
    }
}
