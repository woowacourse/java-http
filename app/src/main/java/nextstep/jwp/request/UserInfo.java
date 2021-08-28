package nextstep.jwp.request;

public class UserInfo {

    private final String account;
    private final String password;

    public UserInfo(String account, String password) {
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
