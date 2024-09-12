package com.techcourse.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.model.User;

public class InMemoryUserRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
	private static final Map<String, User> database = new ConcurrentHashMap<>();

	static {
		final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
		database.put(user.getAccount(), user);
	}

	public static void save(User user) {
		if (database.containsKey(user.getAccount())) {
			throw new IllegalArgumentException("account is already exist");
		}
		database.put(user.getAccount(), user);
	}

	public static Optional<User> findByAccount(String account) {
        if (account == null) {
            log.info("account not exist");
            return Optional.empty();
        }
		return Optional.ofNullable(database.get(account));
	}

	private InMemoryUserRepository() {
	}
}
