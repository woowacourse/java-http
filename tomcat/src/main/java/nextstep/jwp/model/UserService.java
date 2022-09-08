package nextstep.jwp.model;

import static nextstep.jwp.exception.ExceptionType.INVALID_HTTP_REGISTER_EXCEPTION;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidHttpRequestException;

public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private static final String ID = "account";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email";
	private static Long autoIncrementCount = 1L;

	private UserService() {
	}

	public static boolean login(HttpRequest request) {
		if (request.hasCookie() && SessionManager.hasSession(request.getCookie())) {
			return findUserBySession(request);
		}

		return false;
	}

	public static boolean findUserBySession(HttpRequest request) {
		return SessionManager.hasSession(request.getCookie());
	}

	public static User login(Map<String, String> params) {
		if (params.isEmpty()) {
			return null;
		}
		final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get(ID));
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (user.checkPassword(params.get(PASSWORD))) {
				loggingInfoUser(params, user);
				return user;
			}
		}

		return null;
	}

	public static User register(Map<String, String> params) {
		if (params.isEmpty() || params.size() < 3) {
			throw new InvalidHttpRequestException(INVALID_HTTP_REGISTER_EXCEPTION);
		}
		User user = getUser(params);
		InMemoryUserRepository.save(user);
		return user;
	}

	private static User getUser(Map<String, String> params) {
		return new User(++autoIncrementCount, params.get(ID), params.get(PASSWORD), params.get(EMAIL));
	}

	private static void loggingInfoUser(Map<String, String> params, User user) {
		if (user.checkPassword(params.get(PASSWORD))) {
			log.info(" 로그인 성공! 아이디 : " + user.getAccount());
		}
	}
}
