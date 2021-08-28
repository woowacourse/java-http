package nextstep.jwp.model;

import java.util.Map;
import java.util.Objects;

public class User {

    private static final String INSUFFICIENT_FIELD_ERROR_FORMAT = "필수값이 없어서 User 생성에 실패했습니다 (%s: %s)";

    private final long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(0, account, password, email);
    }

    public User(long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static User createWithMap(Map<String, String> map) {
        String account = map.get("account");
        String password = map.get("password");
        String email = map.get("email");
        validateRequiredField(account, password, email);
        return new User(account, password, email);
    }

    private static void validateRequiredField(String account, String password, String email) {
        Objects.requireNonNull(account, String.format(INSUFFICIENT_FIELD_ERROR_FORMAT, "account", account));
        Objects.requireNonNull(password, String.format(INSUFFICIENT_FIELD_ERROR_FORMAT, "password", password));
        Objects.requireNonNull(email, String.format(INSUFFICIENT_FIELD_ERROR_FORMAT, "email", email));
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
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
