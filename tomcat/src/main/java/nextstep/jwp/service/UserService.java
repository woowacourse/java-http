package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public void singUp(final User user) {
        InMemoryUserRepository.save(user);
    }

    private UserService() {
    }

    public static UserService getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final UserService instance = new UserService();
    }
}
