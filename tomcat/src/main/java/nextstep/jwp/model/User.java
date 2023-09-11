package nextstep.jwp.model;

import java.util.Map;

public class User {

    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(final Long id, final String account, final String password, final String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(final String account, final String password, final String email) {
        this(null, account, password, email);
    }

    public User(final Map<String, String> userInfo) {
        this(null, userInfo.get(ACCOUNT_KEY), userInfo.get(PASSWORD_KEY), userInfo.get(EMAIL_KEY));
    }

    public boolean checkPassword(final User user) {
        return this.password.equals(user.password);
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
