package nextstep.jwp.model;

import org.apache.commons.lang3.StringUtils;

import javax.security.auth.login.LoginException;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) throws LoginException {
        validateUser(account, password, email);

        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) throws LoginException {
        this(null, account, password, email);
    }

    private void validateUser(final String account, final String password, final String email) throws LoginException {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
            throw new LoginException();
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public Long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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
