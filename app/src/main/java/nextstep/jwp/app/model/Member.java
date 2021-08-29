package nextstep.jwp.app.model;

public class Member {

    private long id;
    private final String account;
    private final String password;
    private final String email;

    public Member(String account, String password, String email) {
        this(0, account, password, email);
    }

    public Member(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public boolean invalidPassword(String password) {
        return !checkPassword(password);
    }

    public String getAccount() {
        return account;
    }

    public void setId(long id) {
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
