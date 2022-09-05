package nextstep.jwp.db;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.AccountDuplicateException;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

	private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

	private static final Map<String, User> database = new ConcurrentHashMap<>();
	private static final AtomicLong AUTO_INCREMENT = new AtomicLong(0L);

	private InMemoryUserRepository() {
	}

	public static void save(User user) {
		injectId(user);
		String account = user.getAccount();
		validateDuplicateAccount(account);
		database.put(account, user);
	}

	private static void injectId(User user) {
		try {
			Field id = user.getClass().getDeclaredField("id");
			id.setAccessible(true);
			id.set(user, AUTO_INCREMENT.addAndGet(1));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static void validateDuplicateAccount(String account) {
		if (database.containsKey(account)) {
			throw new AccountDuplicateException("같은 account로는 회원가입하지 못합니다.");
		}
	}

	public static Optional<User> findByAccount(String account) {
		return Optional.ofNullable(database.get(account));
	}
}
