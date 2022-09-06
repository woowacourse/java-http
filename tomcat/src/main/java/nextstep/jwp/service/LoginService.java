package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginException;
import nextstep.jwp.model.User;

public class LoginService {

	public static User login(String account, String password) {
		return InMemoryUserRepository.findByAccount(account)
			.filter(member -> member.checkPassword(password))
			.orElseThrow(() -> new InvalidLoginException("로그인 정보가 일치하지 않습니다."));
	}
}
