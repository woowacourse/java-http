package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public String handle(final HttpRequest httpRequest) {
        final Map<String, String> queryParams = httpRequest.getQueryParams();
        validateQueryParams(queryParams);

        InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .ifPresentOrElse(it -> validateUserLogin(queryParams, it), () -> {
                    throw new IllegalArgumentException("User not found");
                });

        return "/login.html";
    }

    private void validateQueryParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("No Parameters");
        }
    }

    private void validateUserLogin(final Map<String, String> queryParamMap, final User it) {
        if (!it.checkPassword(queryParamMap.get("password"))) {
            throw new IllegalArgumentException("User not found");
        }

        log.info(it.toString());
    }
}
