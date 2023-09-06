package nextstep.jwp.model;

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

    private void validate(String account, String password, String email) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("계정은 빈 값이 될 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 빈 값이 될 수 없습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 빈 값이 될 수 없습니다.");
        }
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
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
