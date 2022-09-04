package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;

public class LoginService {

	public static boolean login(String account, String password) {
		return InMemoryUserRepository.findByAccount(account)
			.filter(member -> member.checkPassword(password)).isPresent();
	}
}
