package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.mark.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void saveUser(final User user) {
        if (isExistUser(user)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다 !");
        }
        InMemoryUserRepository.save(user);
        log.info("Create User: {}", user);
    }

    private boolean isExistUser(final User user) {
        return InMemoryUserRepository.findByAccount(user.getAccount()).isPresent() ||
                InMemoryUserRepository.findByEmail(user.getEmail()).isPresent();
    }
}
