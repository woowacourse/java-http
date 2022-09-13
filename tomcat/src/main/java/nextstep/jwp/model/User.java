package nextstep.jwp.model;

import java.util.Objects;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        validateAccount(account);
        validatePassword(password);
        validateEmail(email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validateAccount(final String account) {
        if (account == null) {
            throw new IllegalArgumentException("[ERROR] Account 가 Null 입니다.");
        }
    }

    private void validatePassword(final String password) {
        if (password == null) {
            throw new IllegalArgumentException("[ERROR] Password 가 Null 입니다.");
        }
    }

    private void validateEmail(final String email) {
        if (email == null) {
            throw new IllegalArgumentException("[ERROR] Email 이 Null 입니다.");
        }
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getAccount() {
        return account;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(account, user.account)
                && Objects.equals(password, user.password) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, password, email);
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
