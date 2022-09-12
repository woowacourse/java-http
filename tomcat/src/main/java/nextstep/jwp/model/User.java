package nextstep.jwp.model;

import nextstep.jwp.exception.InputEmptyException;

public class User {

	private Long id;
	private final String account;
	private final String password;
	private final String email;

	public User(Long id, String account, String password, String email) {
		validateUserInfo(account, password, email);
		this.id = id;
		this.account = account;
		this.password = password;
		this.email = email;
	}

	public User(String account, String password, String email) {
		this(null, account, password, email);
	}

	private void validateUserInfo(String account, String password, String email) {
		if (account.isBlank() || password.isBlank() || email.isBlank()) {
			throw new InputEmptyException("회원 정보는 비어있을 수 없습니다.");
		}
	}

	public boolean checkPassword(String password) {
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
