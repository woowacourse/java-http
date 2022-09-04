package nextstep.jwp.service.dto;

import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class LoginDto {

    private final String account;
    private final String password;

    public LoginDto(String account, String password) {
        if (account.isBlank() || password.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
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
