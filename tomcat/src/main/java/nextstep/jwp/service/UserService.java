package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

	private UserService() {
	}

	public static void register(String account, String password, String email) {
		User user = new User(account, password, email);
		InMemoryUserRepository.save(user);
	}
}
