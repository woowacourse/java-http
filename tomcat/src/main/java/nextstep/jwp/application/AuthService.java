package nextstep.jwp.application;

import nextstep.jwp.application.dto.UserDto;
import nextstep.jwp.domain.model.User;
import nextstep.jwp.domain.model.UserRepository;
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.infrastructure.InMemoryUserRepository;

public class AuthService {

    private final UserRepository userRepository = InMemoryUserRepository.getInstance();

    public UserDto login(final String account, final String password) {
        final User user = userRepository.findByAccount(account)
                .orElseThrow(LoginFailException::new);

        if (!user.checkPassword(password)) {
            throw new LoginFailException();
        }

        return UserDto.from(user);
    }
}
