package nextstep.jwp.model;

import com.google.common.base.Strings;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public User(Long id, String account, String password, String email) {
        validateNoneNull(account, password, email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validateNoneNull(String account, String password, String email) {
        if (Strings.isNullOrEmpty(account) || Strings.isNullOrEmpty(password) ||
                Strings.isNullOrEmpty(email)) {
            throw new IllegalArgumentException("필수 값이 null일 수 없습니다.");
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getAccount() {
        return account;
    }

    public User createNewToSave(Long id) {
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
