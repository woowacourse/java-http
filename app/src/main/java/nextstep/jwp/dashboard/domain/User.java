package nextstep.jwp.dashboard.domain;

import nextstep.jwp.httpserver.exception.AuthorizationException;

public class User {
    private Long id;
    private String account;
    private String password;
    private String email;

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public void checkPassword(String password) {
        if (!this.password.equals(password)) {
            throw new AuthorizationException("아이디나 비밀번호가 올바르지 않습니다.");
        }
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
