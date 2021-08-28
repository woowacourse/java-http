package nextstep.jwp.dashboard.service;

import nextstep.jwp.dashboard.controller.dto.UserDto;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.dashboard.repository.InMemoryUserRepository;
import nextstep.jwp.httpserver.exception.AuthorizationException;
import nextstep.jwp.httpserver.exception.DuplicatedException;
import nextstep.jwp.httpserver.exception.NotFoundException;

public class UserService {

    public UserDto login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                                          .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        if (!user.checkPassword(password)) {
            throw new AuthorizationException("아이디나 비밀번호가 올바르지 않습니다.");
        }

        return new UserDto(user);
    }

    public void join(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account)
                              .ifPresent(user -> {
                                  throw new DuplicatedException("이미 가입된 유저입니다.");
                              });

        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
