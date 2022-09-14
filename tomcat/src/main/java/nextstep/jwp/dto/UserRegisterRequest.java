package nextstep.jwp.dto;

public class UserRegisterRequest {
    private final String account;
    private final String password;
    private final String email;

    public UserRegisterRequest(final String account, final String password, final String email) {
        this.account = account;
        this.password = password;
        this.email = email;
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
}
