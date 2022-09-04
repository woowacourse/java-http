package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.model.User;
import nextstep.jwp.support.QueryStringConverter;
import nextstep.jwp.support.Resource;
import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public ResponseEntity execute(final RequestEntity requestEntity) throws Exception {
        final LoginRequest loginRequest = convert(requestEntity.getQueryString());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());

        final Resource resource = new Resource("/login.html");
        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                return new ResponseEntity(HttpStatus.OK, resource.getContentType(), resource.read());
            }
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST, resource.getContentType(), resource.read());
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }
}
