package nextstep.jwp.model;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    private User(final Long id, final String account, final String password, final String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static User register(final String account, final String password, final String email) {
        return new User(null, account, password, email);
    }

    public static User register(final Long id, final String account, final String password, final String email) {
        return new User(id, account, password, email);
    }

    public boolean checkPassword(final String password) {
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
