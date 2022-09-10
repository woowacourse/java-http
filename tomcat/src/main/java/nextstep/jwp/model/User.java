package nextstep.jwp.model;

import nextstep.jwp.exception.badRequest.RegisterInvalidParameterException;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        validateAccount(account);
        validatePassword(password);
        validateEmail(email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    private void validateAccount(String account) {
        if (account == null || account.isBlank()) {
            throw new RegisterInvalidParameterException(account + "은 회원가입에 부적절한 account 입니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new RegisterInvalidParameterException(password + "은 회원가입에 부적절한 password 입니다.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegisterInvalidParameterException(email + "은 회원가입에 부적절한 email 입니다.");
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
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
