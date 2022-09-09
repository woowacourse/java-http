package nextstep.jwp.domain;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByAccount(String account);
}
