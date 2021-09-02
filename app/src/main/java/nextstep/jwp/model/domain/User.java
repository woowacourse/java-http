package nextstep.jwp.model.domain;

public class User {

    private static long count = 0;

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this.id = ++count;
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
