package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.QueryStringConverter;
import nextstep.jwp.http.RequestEntity;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.model.User;
import nextstep.jwp.support.*;
import org.apache.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public ResponseEntity execute(final RequestEntity requestEntity) {
        if (isQueryStringNotExist(requestEntity.getQueryString())) {
            final Resource resource = new Resource(View.LOGIN.getValue());
            final Headers headers = new Headers();
            headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
            return new ResponseEntity(headers).content(resource.read());
        }

        final LoginRequest loginRequest = convert(requestEntity.getQueryString());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());

        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                final Headers headers = new Headers();
                headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
                return new ResponseEntity(headers).httpStatus(HttpStatus.FOUND);
            }
        }
        throw new UnauthorizedException();
    }

    private boolean isQueryStringNotExist(final String queryString) {
        return queryString == null;
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }
}
