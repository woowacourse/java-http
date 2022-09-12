package nextstep.jwp.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginException;
import nextstep.jwp.model.User;
import support.DatabaseIsolation;

class LoginServiceTest extends DatabaseIsolation {

	@DisplayName("로그인에 성공하면 user를 반환한다.")
	@Test
	void login() {
		// given
		String account = "does";
		String password = "does!";
		User user = new User(account, password, "does@mail.com");
		InMemoryUserRepository.save(user);

		// when
		User loginUser = LoginService.login(account, password);

		// then
		assertThat(user).isEqualTo(loginUser);
	}

	@DisplayName("로그인에 실패하면 예외가 발생한다.")
	@Test
	void login_fail() {
		// given
		String account = "does";
		String password = "does!";

		// when then
		assertThatThrownBy(() -> LoginService.login(account, password))
			.isInstanceOf(InvalidLoginException.class)
			.hasMessage("로그인 정보가 일치하지 않습니다.");
	}
}
