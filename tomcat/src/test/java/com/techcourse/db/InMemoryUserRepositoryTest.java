package com.techcourse.db;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.model.User;

class InMemoryUserRepositoryTest {

	@DisplayName("이미 존재하는 계정이면 예외를 던진다.")
	@Test
	void save_whenExistingAccount() {
		User user = new User("gugu", "password", "hi@hi.com");

		assertThatThrownBy(() -> InMemoryUserRepository.save(user))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("account is already exist");
	}
}
