package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.controller.dto.UserRegisterRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.web.ResponseEntity;

public class UserCreateController {

    public ResponseEntity<String> doPost(final UserRegisterRequest request) {
        Optional<User> user = InMemoryUserRepository.findByAccount(request.getAccount());
        if (user.isPresent()) {
            throw new InvalidUserException("이미 존재하는 계정입니다.");
        }

        InMemoryUserRepository.save(request.toDomain());
        return new ResponseEntity<>(HttpStatus.FOUND, "index.html");
    }
}
