package nextstep.jwp.application;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.request.LoginRequest;
import nextstep.jwp.dto.request.RegisterRequest;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.model.User;

public class MemberService {
    public void login(final LoginRequest loginRequest) {
        final Optional<User> user = findByAccount(loginRequest.getAccount());
        if (user.isPresent()) {
            if (!user.get().checkPassword(loginRequest.getPassword())) {
                throw new AuthenticationException();
            }
        }
    }

    public void register(final RegisterRequest registerRequest) {
        final Optional<User> user = findByAccount(registerRequest.getAccount());
        if (user.isPresent()) {
            throw new DuplicateAccountException();
        }
        InMemoryUserRepository.save(registerRequest.toUser());
    }
}
