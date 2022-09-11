package nextstep.jwp.model;

public class UserRequest {

    private String account;
    private String password;
    private String email;

    public UserRequest(final String account, final String password, final String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public UserRequest(final String account, final String password) {
        this(account, password, null);
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
