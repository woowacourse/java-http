package nextstep.jwp.model;

import nextstep.jwp.exception.UnauthorizedException;

public class User {

    private long id;
    private final String account;
    private final String password;
    private final String email;

    public User(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(0, account, password, email);
    }

    public void checkPassword(String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
    }

    public String getAccount() {
        return account;
    }

    public long getId() {
        return id;
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
