package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import nextstep.jwp.model.User;

public class InMemoryUserRepository {

	private static final Map<String, User> database = new ConcurrentHashMap<>();
	private static final AtomicLong idCounter = new AtomicLong();

	static {
		final User user = new User(idCounter.incrementAndGet(), "gugu", "password", "hkkang@woowahan.com");
		database.put(user.getAccount(), user);
	}

	private InMemoryUserRepository() {
	}

	public static void save(User user) {
		database.put(user.getAccount(), user.addId(idCounter.incrementAndGet()));
	}

	public static Optional<User> findByAccount(String account) {
		return Optional.ofNullable(database.get(account));
	}
}
