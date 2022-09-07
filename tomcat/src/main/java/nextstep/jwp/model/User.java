package nextstep.jwp.model;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(final Long id, final String account, final String password, final String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(final String account, final String password, final String email) {
        this(null, account, password, email);
    }

    public User(final String account, final String password) {
        this(null, account, password, null);
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
