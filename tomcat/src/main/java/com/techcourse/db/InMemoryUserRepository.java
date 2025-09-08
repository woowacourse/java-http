package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.NONE)
@Slf4j
public class InMemoryUserRepository {

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    static {
        final User user = User.withId(ID_GENERATOR.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        DATABASE.put(user.getAccount(), user);
    }

    public static User save(final User user) {
        if (existsByAccount(user.getAccount())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (user.isPersisted()) {
            DATABASE.put(user.getAccount(), user);
            return user;
        }

        final User persisted = User.withId(
                ID_GENERATOR.getAndIncrement(),
                user.getAccount(),
                user.getPassword(),
                user.getEmail());
        DATABASE.put(persisted.getAccount(), persisted);
        return persisted;
    }

    public static Optional<User> findByAccount(final String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }

    public static boolean existsByAccount(final String account) {
        return DATABASE.containsKey(account);
    }
}
