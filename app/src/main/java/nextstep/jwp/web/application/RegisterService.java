package nextstep.jwp.web.application;

import nextstep.jwp.server.exception.InternalServerException;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.model.User;

public class RegisterService {

    public void register(String account, String password, String email) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new InternalServerException();
        }
        InMemoryUserRepository.save(new User(null, account, password, email));
    }
}
