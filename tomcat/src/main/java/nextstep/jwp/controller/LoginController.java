package nextstep.jwp.controller;

import nextstep.jwp.ResourceReader;
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

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public ResponseEntity execute(final RequestEntity requestEntity) throws Exception {
        final LoginRequest loginRequest = convert(requestEntity.getQueryString());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());

        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            log.debug(user.toString());
            if (user.isSamePassword(loginRequest.getPassword())) {
                return new ResponseEntity(HttpStatus.OK, requestEntity.getContentType(), new ResourceReader().read("/login.html"));
            }
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST, requestEntity.getContentType(), null);
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = new HashMap<>();
        for (String info : queryString.split("&")) {
            final String[] keyValue = info.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("key value 값이 올바르지 않음");
            }
            final String key = keyValue[0];
            final String value = keyValue[1];
            paramMapping.put(key, value);
        }
        return new LoginRequest(paramMapping.get("account"), paramMapping.get("password"));
    }

}
