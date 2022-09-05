package nextstep.jwp.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.DatabaseIsolation;

class UserServiceTest extends DatabaseIsolation {

	@DisplayName("회원 가입에 성공하면 true를 반환한다.")
	@Test
	void register_true() {
		// when
		boolean result = UserService.register("gugu", "password", "does@gmail.com");

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("중복 account이면 false를 반환한다.")
	@Test
	void register_false() {
		// given
		UserService.register("does", "password", "does@gmail.com");

		// when
		boolean result = UserService.register("does", "password", "does@gmail.com");

		// then
		assertThat(result).isFalse();
	}
}
