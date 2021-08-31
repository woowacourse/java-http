package nextstep.jwp.model;

import nextstep.jwp.exception.UnauthorizedException;

public class User {

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(String password) {
        if (this.password.equals(password)) {
            return true;
        }
        throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
    }

    public String getAccount() {
        return account;
    }

    public long getId() {
        return id;
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
