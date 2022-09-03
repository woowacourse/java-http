package nextstep.jwp.service;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Parameters;

public class UserService {

    public Optional<User> findUser(Parameters parameters) {
        if (parameters.contains("account")) {
            return InMemoryUserRepository.findByAccount(parameters.get("account"));
        }
        return Optional.empty();
    }
}
