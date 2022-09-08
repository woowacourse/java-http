package nextstep.jwp.model;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;

public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private static final String ID = "account";
	private static final String PASSWORD = "password";

	private UserService() {
	}

	public static void info(Map<String, String> params) {
		if (params.isEmpty()) {
			return;
		}
		final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get(ID));
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			loggingInfoUser(params, user);
		}
	}

	public static boolean login(Map<String, String> params) {
		if (params.isEmpty()) {
			return false;
		}
		final Optional<User> user = InMemoryUserRepository.findByAccount(params.get(ID));
		return user.isPresent() && user.get().checkPassword(params.get(PASSWORD));
	}

	private static void loggingInfoUser(Map<String, String> params, User user) {
		if (user.checkPassword(params.get(PASSWORD))) {
			log.info("user : " + user.toString());
		}
	}
}
