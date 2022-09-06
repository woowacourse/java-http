package nextstep.jwp.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AccountDuplicateException;
import support.DatabaseIsolation;

class UserServiceTest extends DatabaseIsolation {

	@DisplayName("회원 가입에 성공하면 true를 반환한다.")
	@Test
	void register_true() {
		// when
		UserService.register("gugu", "password", "does@gmail.com");

		// then
		assertThat(InMemoryUserRepository.findByAccount("gugu")).isPresent();
	}

	@DisplayName("중복 account이면 false를 반환한다.")
	@Test
	void register_false() {
		// given
		UserService.register("does", "password", "does@gmail.com");

		// when //then
		assertThatThrownBy(() -> UserService.register("does", "password", "does@gmail.com"))
			.isInstanceOf(AccountDuplicateException.class)
			.hasMessage("같은 account로는 회원가입하지 못합니다.");
	}
}
