package nextstep.jwp.model;

import java.util.Objects;

public class User {

    private final String account;
    private final String password;
    private final String email;
    private long id;

    public User(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
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

    public boolean isEmptyId() {
        return Objects.isNull(id);
    }

    public void insertId(long id) {
        this.id = id;
    }
}
