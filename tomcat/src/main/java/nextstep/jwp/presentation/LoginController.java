package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryStrings;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final HttpRequest httpRequest;

    public LoginController(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String login() {
        QueryStrings queryStrings = QueryStrings.of(httpRequest.getUri().getValue());
        String account = queryStrings.find("account");
        String password = queryStrings.find("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("not found account"));
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
        return "/login.html";
    }
}
