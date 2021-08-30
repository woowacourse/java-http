package nextstep.jwp.dashboard.service;

import nextstep.jwp.dashboard.controller.dto.UserDto;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.dashboard.repository.InMemoryUserRepository;
import nextstep.jwp.httpserver.exception.DuplicatedException;
import nextstep.jwp.httpserver.exception.NotFoundException;

public class UserService {

    private final InMemoryUserRepository inMemoryUserRepository;

    public UserService(InMemoryUserRepository inMemoryUserRepository) {
        this.inMemoryUserRepository = inMemoryUserRepository;
    }

    public UserDto login(String account, String password) {
        User user = inMemoryUserRepository.findByAccount(account)
                                          .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        user.checkPassword(password);
        return new UserDto(user);
    }

    public void join(String account, String password, String email) {
        inMemoryUserRepository.findByAccount(account)
                              .ifPresent(user -> {
                                  throw new DuplicatedException("이미 가입된 유저입니다.");
                              });

        final User user = new User(account, password, email);

        inMemoryUserRepository.save(user);
    }
}
