package repository;

import nextstep.jwp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserRepository {

    private final Map<Long, User> userById = new HashMap<>();

    private static final InMemoryUserRepository repository = new InMemoryUserRepository();

    private InMemoryUserRepository() {
        userById.put(1L, new User(1L, "mint", "mintpw", "email1"));
        userById.put(2L, new User(2L, "poi", "poipw", "email2"));
        userById.put(3L, new User(3L, "jude", "judepw", "email3"));
    }

    public static InMemoryUserRepository getInstance() {
        return repository;
    }

    public List<User> findAll() {
        return new ArrayList<>(userById.values());
    }
}
