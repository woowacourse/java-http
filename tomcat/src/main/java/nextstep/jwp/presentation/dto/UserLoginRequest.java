package nextstep.jwp.presentation.dto;

import org.apache.coyote.http11.web.QueryParameters;

public class UserLoginRequest {

    private final String account;
    private final String password;

    public UserLoginRequest(final String account, final String password) {
        this.account = account;
        this.password = password;
    }

    public static UserLoginRequest from(final QueryParameters queryParameters) {
        final String account = queryParameters.getValueByKey("account");
        final String password = queryParameters.getValueByKey("password");

        return new UserLoginRequest(account, password);
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
