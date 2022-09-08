package nextstep.jwp.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.util.StringUtil;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        validate(account, password, email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    private void validate(String account, String password, String email) {
        if (StringUtil.isNullOrBlank(account) ||
                StringUtil.isNullOrBlank(password) ||
                StringUtil.isNullOrBlank(email)) {
            throw new IllegalArgumentException("account, password or email is null or blank");
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public boolean hasId() {
        return this.id != null;
    }

    public String getAccount() {
        return account;
    }

    public User setId(long id) {
        return new User(id, account, password, email);
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
