package com.techcourse.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

	@DisplayName("User 객체 생성 테스트")
	@Nested
	class UserCreationTest {

		@DisplayName("User 객체를 생성한다.")
		@Test
		void create() {
			String account = "sangdol";
			String password = "123";
			String email = "sangdol@email.com";

			User user = new User(account, password, email);

			assertThat(user.getAccount()).isEqualTo(account);
			assertThat(user.checkPassword(password)).isTrue();
		}

		@DisplayName("account가 null이거나 빈 문자열인 경우 예외를 던진다.")
		@Test
		void create_whenInvalidAccount() {
			runInvalidValueTest(null, "123", "email@email.com");
		    runInvalidValueTest("", "123", "email@email.com");
		}

		@DisplayName("password가 null이거나 빈 문자열인 경우 예외를 던진다.")
		@Test
		void create_whenInvalidPassword() {
			runInvalidValueTest("sangdol", null, "email@email.com");
			runInvalidValueTest("sangdol", "", "email@email.com");
		}

		@DisplayName("email이 null이거나 빈 문자열인 경우 예외를 던진다.")
		@Test
		void create_whenInvalidEmail() {
			runInvalidValueTest("sangdol", "123", null);
			runInvalidValueTest("sangdol", "123", "");
		}

		void runInvalidValueTest(String account, String password, String email) {
		    assertThatThrownBy(() -> new User(account, password, email))
			    .isInstanceOf(IllegalArgumentException.class);
		}
	}

	@DisplayName("User 객체 비밀번호 확인 테스트")
	@Nested
	class UserCheckPasswordTest {

		@DisplayName("비밀번호가 일치하는 경우 true를 반환한다.")
		@Test
		void checkPassword() {
			String password = "123";
			User user = new User("sangdol", password, "email@email.com");

			assertThat(user.checkPassword(password)).isTrue();
		}

		@DisplayName("비밀번호가 일치하지 않으면 true를 반환한다.")
		@Test
		void checkPassword_WhenWrongPassword() {
			String password = "123";
			User user = new User("sangdol", password, "email@email.com");

			assertThat(user.checkPassword(password + "fail")).isFalse();
		}

		@DisplayName("비밀번호가 null인 경우 false를 반환한다.")
		@Test
		void checkPassword_WhenNullPassword() {
			String password = "123";
			User user = new User("sangdol", password, "email@email.com");

			assertThat(user.checkPassword(null)).isFalse();
		}
	}
}
