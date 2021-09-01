package nextstep.jwp.model;

public class User {

    private final String account;
    private final String password;
    private final String email;
    private long id;

    public User(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
