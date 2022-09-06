package nextstep.jwp.model;

import nextstep.jwp.exception.NotEnoughConditionException;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = validateNullOrBlank(account);
        this.password = validateNullOrBlank(password);
        this.email = validateNullOrBlank(email);
    }

    private String validateNullOrBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new NotEnoughConditionException();
        }
        return value;
    }

    public static User from(final Long id, final User user) {
        return new User(id, user.account, user.password, user.email);
    }

    public boolean isSamePassword(final String password) {
        return this.password.equals(password);
    }

    public Long getId() {
        return id;
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
