package com.techcourse.db;

import com.techcourse.model.Account;
import com.techcourse.model.Email;
import com.techcourse.model.Password;
import com.techcourse.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<Account, User> database = new ConcurrentHashMap<>();

    static {
        final Account account = new Account("gugu");
        final Password password = new Password("password");
        final Email email = new Email("hkkang@woowahan.com");
        final User user = new User(1L, account, password, email);
        database.put(account, user);
    }

    public static void save(final User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(final Account account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {}
}
