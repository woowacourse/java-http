package nextstep.jwp.model;

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
        if (account.isBlank()) {
            throw new IllegalArgumentException("account가 빈 값입니다.");
        }
    }

    private void validatePassword(String password) {
        if (password.isBlank()) {
            throw new IllegalArgumentException("password가 빈 값입니다.");
        }
    }

    private void validateEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("email 빈 값입니다.");
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
