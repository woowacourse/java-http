package nextstep.jwp.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private Long id;
    private final Map<Long, User> database;

    private InMemoryUserRepository(Map<Long, User> database, Long initialId) {
        this.database = database;
        this.id = initialId;
    }

    public static InMemoryUserRepository init() {
        var repository = new InMemoryUserRepository(new ConcurrentHashMap<>(), 1L);
        repository.save(new User("gugu", "password", "hkkang@woowahan.com"));
        return repository;
    }

    public static InMemoryUserRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    public synchronized Long save(User user) {
        database.put(id, user);
        user.setId(id);
        return id++;
    }

    public Optional<User> findByAccount(String account) {
        return database.values().stream()
                .filter(user -> user.getAccount().equals(account))
                .findAny();
    }

    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }

    public void deleteAll() {
        database.clear();
    }

    private static class LazyHolder {
        private static final InMemoryUserRepository INSTANCE = InMemoryUserRepository.init();
    }
}
