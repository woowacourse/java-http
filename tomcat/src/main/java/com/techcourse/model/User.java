package com.techcourse.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {

	private static final Logger log = LoggerFactory.getLogger(User.class);
	private final Long id;
	private final String account;
	private final String password;
	private final String email;

	public User(Long id, String account, String password, String email) {
		validateIsNotEmptyValue(account, password, email);
		this.id = id;
		this.account = account;
		this.password = password;
		this.email = email;
	}

	private void validateIsNotEmptyValue(String account, String password, String email) {
		if (account == null || account.isBlank()) {
			throw new IllegalArgumentException("Account is empty");
		}
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("Password is empty");
		}
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email is empty");
		}
	}

	public User(String account, String password, String email) {
		this(null, account, password, email);
	}

	public boolean checkPassword(String password) {
		if (password == null) {
			log.info("password not exist");
			return false;
		}
		return this.password.equals(password);
	}

	public String getAccount() {
		return account;
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", account='" + account + '\'' +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			'}';
	}
}
