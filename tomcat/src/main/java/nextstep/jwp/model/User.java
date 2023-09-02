package nextstep.jwp.model;

import java.util.Objects;

public class User {

    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean hasSameCredential(String account, String password) {
        return this.account.equals(account) && this.password.equals(password);
    }

    public String getAccount() {
        return account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(account, user.account) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, password, email);
    }
}
