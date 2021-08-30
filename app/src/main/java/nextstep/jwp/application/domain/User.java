package nextstep.jwp.application.domain;

import java.util.Objects;

public class User {

    private final Long id;
    private final Account account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(null, new Account(account), password, email);
    }

    public User(Long id, Account account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public User toIdentifiedEntity(Long id) {
        return new User(id, account, password, email);
    }

    public Long id() {
        return id;
    }

    public Account account() {
        return account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
