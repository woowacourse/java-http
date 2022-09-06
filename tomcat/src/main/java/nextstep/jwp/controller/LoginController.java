package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.*;
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

    private final IdGenerator idGenerator;

    public LoginController(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Response execute(final Request request) {
        final LoginRequest loginRequest = convert(request.getBody());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());

        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                final Headers headers = makeHeaders(request);
                return makeResponse(headers);
            }
        }
        throw new UnauthorizedException();
    }

    private Headers makeHeaders(final Request request) {
        final Headers headers = new Headers();
        if (isRequestNotContainsSessionId(request)) {
            final HttpCookie responseCookie = HttpCookie.of();
            responseCookie.put("JSESSIONID", idGenerator.generate());
            headers.put(HttpHeader.SET_COOKIE, responseCookie.parse());
        }
        return headers;
    }

    private boolean isRequestNotContainsSessionId(final Request request) {
        final String cookie = request.getHeaders().find(HttpHeader.COOKIE);
        HttpCookie httpCookie = HttpCookie.of(cookie);
        final String jsessionid = httpCookie.findByKey("JSESSIONID");
        return jsessionid == null;
    }

    private Response makeResponse(final Headers headers) {
        headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
        return new Response(headers).httpStatus(HttpStatus.FOUND);
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }
}
