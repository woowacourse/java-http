package org.apache.coyote.http11.dto;

public class JoinQueryDto {
    private final String account;
    private final String email;
    private final String password;

    public JoinQueryDto(String account, String email, String password) {
        this.account = account;
        this.email = email;
        this.password = password;
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
