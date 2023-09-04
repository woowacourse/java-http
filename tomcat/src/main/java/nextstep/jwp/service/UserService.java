package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.db.SessionRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.UUID;

public class UserService {

    public UUID login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(HttpStatus.UNAUTHORIZED.getStatusName()));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException(HttpStatus.UNAUTHORIZED.getStatusName());
        }
        System.out.println("로그인 성공! ID: " + user.getAccount());
        return SessionRepository.save(user.getId());
    }

    public UUID register(final String account, final String password, final String email) {
        if (!InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException(HttpStatus.UNAUTHORIZED.name());
        }
        final long saveId = InMemoryUserRepository.save(new User(account, password, email));
        System.out.println("환영합니다! " + account + "님");
        return SessionRepository.save(saveId);
    }
}
