package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class HttpService {

    public Boolean isAuthorized(Map<String, String> params) {
        Optional<User> account = InMemoryUserRepository.findByAccount(params.get("account"));
        return account.isPresent();
    }

    public void register(Map<String, String> params) {
        final User user = new User(InMemoryUserRepository.size() + 1, params.get("account"), params.get("password"),
                params.get("email"));
        InMemoryUserRepository.save(user);
    }
}
