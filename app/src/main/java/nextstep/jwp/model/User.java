package nextstep.jwp.model;

public class User {

    private long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(long id, String account, String password, String email) {
        this(account, password, email);
        this.id = id;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public long getId() {
        return id;
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
