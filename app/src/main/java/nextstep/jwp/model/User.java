package nextstep.jwp.model;

import nextstep.jwp.exception.IllegalOperationException;
import nextstep.jwp.exception.UnAuthorizedException;

public class User {

    private Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public void validatePassword(String requestPassword) {
        if (!password.equals(requestPassword)) {
            throw new UnAuthorizedException("비밀번호가 일치하지 않습니다.");
        }
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalOperationException("id가 이미 할당된 경우에는 재할당 할 수 없습니다.");
        }
        this.id = id;
    }

    public boolean hasSameEmail(String otherEmail) {
        return email.equals(otherEmail);
    }

    public Long getId() {
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
