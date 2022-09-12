package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InputEmptyException;
import nextstep.jwp.exception.InvalidLoginException;
import nextstep.jwp.model.User;

public class LoginService {

	public static User login(String account, String password) {
		validateInputEmpty(account, password);
		return InMemoryUserRepository.findByAccount(account)
			.filter(member -> member.checkPassword(password))
			.orElseThrow(() -> new InvalidLoginException("로그인 정보가 일치하지 않습니다."));
	}

	private static void validateInputEmpty(String account, String password) {
		if (account.isBlank() || password.isBlank()) {
			throw new InputEmptyException("로그인 정보를 입력하지 않았습니다.");
		}
	}
}
