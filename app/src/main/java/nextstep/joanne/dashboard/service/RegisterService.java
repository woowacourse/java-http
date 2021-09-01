package nextstep.joanne.dashboard.service;

import nextstep.joanne.dashboard.db.InMemoryUserRepository;
import nextstep.joanne.dashboard.model.User;

public class RegisterService {
    public void join(String account, String email, String password) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
