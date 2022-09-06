package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.QueryStringConverter;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.support.View;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public Response execute(final Request request) {
        final LoginRequest loginRequest = convert(request.getBody());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());

        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                final Headers headers = new Headers();
                headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
                return new Response(headers).httpStatus(HttpStatus.FOUND);
            }
        }
        throw new UnauthorizedException();
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }
}
