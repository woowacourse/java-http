package nextstep.jwp.model;

import nextstep.jwp.exception.UnAuthorizedException;

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

    public User(String account, String password) {
        this(-1L, account, password, null);
    }

    public boolean checkPassword(String password) {
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

    public void validatePassword(String requestPassword) {
        if (!password.equals(requestPassword)) {
            throw new UnAuthorizedException("비밀번호가 일치하지 않습니다.");
        }
    }
}
