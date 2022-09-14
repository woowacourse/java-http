package nextstep.jwp.dto;

public class UserLoginRequest {

    private final String account;
    private final String password;

    public UserLoginRequest(final String account, final String password) {
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
