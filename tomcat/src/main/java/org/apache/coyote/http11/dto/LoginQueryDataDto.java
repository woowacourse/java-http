package org.apache.coyote.http11.dto;

public class LoginQueryDataDto {
    private final String account;
    private final String password;

    public LoginQueryDataDto(String account, String password) {
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
