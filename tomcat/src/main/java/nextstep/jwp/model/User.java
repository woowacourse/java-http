package nextstep.jwp.model;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(final String account, final String password, final String email) {
        this(null, account, password, email);
    }

    public User(final Long id, final String account, final String password, final String email) {
        validate(account, password, email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validate(final String account, final String password, final String email) {
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("올바른 사용자 정보를 입력해주세요.");
        }
    }

    public boolean checkPassword(final String password) {
        return this.password.equals(password);
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
