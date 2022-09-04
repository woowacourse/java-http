package nextstep.jwp.domain.model;

import java.util.Optional;

public interface UserRepository {
    User save(final User user);

    Optional<User> findByAccount(final String account);
}
