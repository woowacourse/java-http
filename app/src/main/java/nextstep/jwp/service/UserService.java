package nextstep.jwp.service;

import nextstep.jwp.controller.dto.UserRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    public UserService() {

    }

    public String login(UserRequest userRequest) {
        final User user = findUserByAccount(userRequest.getAccount());
        if (user.checkPassword(userRequest.getPassword())) {
            return user.toString();
        }
        LOG.info("옳지 않은 비밀번호입니다.");
        throw new IllegalArgumentException("옳지 않은 비밀번호입니다.");
    }

    public void register(UserRequest userRequest) {
        InMemoryUserRepository.save(userRequest.toEntity());
    }

    private User findUserByAccount(String account) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return user.get();
        }
        LOG.info("찾을 수 없는 사용자입니다.");
        throw new IllegalArgumentException("찾을 수 없는 사용자입니다.");
    }
}
