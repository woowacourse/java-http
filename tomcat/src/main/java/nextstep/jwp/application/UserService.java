package nextstep.jwp.application;

import nextstep.jwp.application.dto.UserDto;
import nextstep.jwp.domain.model.User;
import nextstep.jwp.domain.model.UserRepository;
import nextstep.jwp.infrastructure.InMemoryUserRepository;
import org.apache.coyote.http11.exception.NotFoundException;

public class UserService {

    private final UserRepository userRepository = InMemoryUserRepository.getInstance();

    public Long save(final String account, final String password, final String email) {
        final User savedUser = userRepository.save(new User(account, password, email));
        return savedUser.getId();
    }

    public UserDto findByAccount(final String account) {
        final User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("찾는 유저가 없습니다."));

        return UserDto.from(user);
    }
}
