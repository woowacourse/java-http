package nextstep.jwp.model.user;

import nextstep.jwp.db.InMemoryUserRepository;

public class UserService {

    public boolean addNewUser(String account, String email, String password) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return true;
    }
}
