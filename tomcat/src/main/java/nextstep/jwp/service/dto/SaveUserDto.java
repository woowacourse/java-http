package nextstep.jwp.service.dto;

import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class SaveUserDto {

    private final String account;
    private final String password;
    private final String email;

    public SaveUserDto(String account, String password, String email) {
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
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
