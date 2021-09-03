package nextstep.jwp.model;

public class User {

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(0, account, password, email);
    }

    public User(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public void checkPassword(String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new RuntimeException("Invalid Password !!");
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
