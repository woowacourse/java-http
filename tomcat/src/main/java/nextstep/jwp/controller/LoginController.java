package nextstep.jwp.controller;

import nextstep.jwp.Resource;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.model.User;
import org.apache.http.HttpStatus;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private static final String APPEND_DELIMITER = "&";
    private static final String EQUAL_DELIMITER = "=";
    private static final int QUERY_PARAM_KEY_VALUE_SIZE = 2;

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public ResponseEntity execute(final RequestEntity requestEntity) throws Exception {
        final LoginRequest loginRequest = convert(requestEntity.getQueryString());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());

        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                final Resource resource = new Resource("/login.html");
                return new ResponseEntity(HttpStatus.OK, resource.getContentType(), resource.read());
            }
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST, null);
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = new HashMap<>();
        for (String info : queryString.split(APPEND_DELIMITER)) {
            final String[] queryParam = info.split(EQUAL_DELIMITER);
            if (queryParam.length != QUERY_PARAM_KEY_VALUE_SIZE) {
                throw new IllegalArgumentException("key value 값이 올바르지 않음");
            }
            final String key = queryParam[0];
            final String value = queryParam[1];
            paramMapping.put(key, value);
        }
        return new LoginRequest(paramMapping.get("account"), paramMapping.get("password"));
    }

}
