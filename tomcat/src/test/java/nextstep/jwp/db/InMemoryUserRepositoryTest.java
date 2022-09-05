package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.exception.AccountDuplicateException;
import nextstep.jwp.model.User;
import support.DatabaseIsolation;

class InMemoryUserRepositoryTest extends DatabaseIsolation {

	@DisplayName("User를 저장하면 Id가 생성된다.")
	@Test
	void save() {
		// given
		User user = new User("does", "password", "does@gmail.com");

		// when
		InMemoryUserRepository.save(user);

		// then
		assertThat(user).extracting("id").isNotNull();
	}

	@DisplayName("같은 account로는 회원가입하지 못 한다.")
	@Test
	void save_duplicateAccount() {
		// given
		User user = new User("gugu", "password", "does@gmail.com");
		InMemoryUserRepository.save(user);

		// when then
		User newUser = new User("gugu", "password", "does@gmail.com");
		assertThatThrownBy(() -> InMemoryUserRepository.save(newUser))
			.isInstanceOf(AccountDuplicateException.class)
			.hasMessage("같은 account로는 회원가입하지 못합니다.");
	}
}
