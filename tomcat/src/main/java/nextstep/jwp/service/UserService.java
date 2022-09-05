package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AccountDuplicateException;
import nextstep.jwp.model.User;

public class UserService {

	private UserService() {
	}

	public static boolean register(String account, String password, String email) {
		User user = new User(account, password, email);
		try {
			InMemoryUserRepository.save(user);
			return true;
		} catch (AccountDuplicateException e) {
			return false;
		}
	}
}
