package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
        idGenerator.set(2L); // 다음 ID는 2부터 시작
    }

    public static void save(User user) {
        User userToSave = user;
        
        // ID가 없는 경우 자동으로 새로운 ID 할당
        if (user.getId() == null) {
            Long newId = idGenerator.getAndIncrement();
            userToSave = new User(newId, user.getAccount(), user.getPassword(), user.getEmail());
        }
        
        database.put(userToSave.getAccount(), userToSave);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean existsByAccount(String account) {
        return database.containsKey(account);
    }

    private InMemoryUserRepository() {}
}
