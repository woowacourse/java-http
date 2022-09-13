package nextstep.jwp.dto;

public class LoginRequest {

    private final String account;
    private final String password;

    public LoginRequest(final String account, final String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
