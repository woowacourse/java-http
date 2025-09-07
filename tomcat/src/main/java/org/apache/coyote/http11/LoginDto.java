package org.apache.coyote.http11;

public record LoginDto(
        String account,
        String password
) {

    public LoginDto {
        if (account == null || password == null) {
            throw new NullPointerException("로그인 아이디 혹은 패스워드가 입력되지 않았습니다.");
        }
    }
}
