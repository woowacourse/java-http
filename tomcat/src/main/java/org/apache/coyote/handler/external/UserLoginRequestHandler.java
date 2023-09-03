package org.apache.coyote.handler.external;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.request.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserLoginRequestHandler implements RequestHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final QueryParams queryParams = httpRequest.queryParams();
        final String account = queryParams.getParamValue("account");

        final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
        if (maybeUser.isPresent()) {
            log.info("===========> 로그인된 유저 = {}", maybeUser.get());
        }

        return null;
    }
}
