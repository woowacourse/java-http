package nextstep.jwp.webserver.model;

public class User {

    private Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(User user) {
        return this.password.equals(user.password);
    }

    public String getAccount() {
        return account;
    }

    public void assignId(long id) {
        this.id = id;
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
