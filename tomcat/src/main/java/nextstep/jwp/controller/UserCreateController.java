package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.UserLoginRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.ResponseEntity;

public class UserCreateController {

    public ResponseEntity<String> doPost(final UserLoginRequest request) {
        User user = InMemoryUserRepository.findByAccount(request.getAccount())
                .orElseThrow(UserNotFoundException::new);

        if (user.checkPassword(request.getPassword())) {
            return new ResponseEntity<>(HttpStatus.FOUND, "/index.html");
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
