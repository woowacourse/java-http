package nextstep.jwp.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class User {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(0L, account, password, email);
    }

    public User(long id, String account, String password, String email) {
        this.id = id;
        validateNullOrEmpty(account);
        validateNullOrEmpty(password);
        validateNullOrEmpty(email);
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validateNullOrEmpty(String target) {
        if (Objects.isNull(target) || target.isEmpty()) {
            LOG.info("입력값은 null이거나 empty일 수 없습니다.");
            throw new IllegalStateException("입력값은 null이거나 empty일 수 없습니다.");
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
