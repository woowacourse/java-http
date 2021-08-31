package nextstep.jwp.model;

public class User {

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public static User of(final long id, final User user) {
        return new User(id, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public User(final String account, final String password, final String email) {
        this(-1, account, password, email);
    }

    public User(final long id, final String account, final String password, final String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(final String password) {
        return this.password.equals(password);
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
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
