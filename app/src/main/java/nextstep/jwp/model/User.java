package nextstep.jwp.model;

import com.google.common.base.Strings;

public class User {

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(0, account, password, email);
    }

    public User(long id, String account, String password, String email) {
        validateNoneNull(account, password, email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validateNoneNull(String account, String password, String email) {
        if (Strings.isNullOrEmpty(account) || Strings.isNullOrEmpty(password) ||
                Strings.isNullOrEmpty(email)) {
            throw new IllegalArgumentException("필수 값이 null일 수 없습니다.");
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
