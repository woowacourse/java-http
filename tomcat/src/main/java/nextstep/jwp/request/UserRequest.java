package nextstep.jwp.request;

public class UserRequest {

    private final String account;
    private final String password;

    public UserRequest(final String account, final String password) {
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
