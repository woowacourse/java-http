package nextstep.jwp.service.dto;

import org.apache.coyote.support.HttpException;

public class LoginDto {

    private final String account;
    private final String password;

    public LoginDto(String account, String password) {
        if (account.isBlank() || password.isBlank()) {
            throw HttpException.ofBadRequest();
        }
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
