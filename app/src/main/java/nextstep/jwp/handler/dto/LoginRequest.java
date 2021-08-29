package nextstep.jwp.handler.dto;

public class LoginRequest {
    private final String account;
    private final String password;

    public LoginRequest(String account, String password) {
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
