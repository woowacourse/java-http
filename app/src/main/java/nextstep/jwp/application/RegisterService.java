package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InternalServerException;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(String account, String password, String email) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new InternalServerException();
        }
        InMemoryUserRepository.save(new User(null, account, password, email));
    }
}
