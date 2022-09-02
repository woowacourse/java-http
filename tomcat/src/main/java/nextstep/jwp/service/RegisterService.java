package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicationException;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicationException("이미 존재하는 아이디입니다.");
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
