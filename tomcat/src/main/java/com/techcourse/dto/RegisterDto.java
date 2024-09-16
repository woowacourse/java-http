package com.techcourse.dto;

import java.util.StringTokenizer;

public record RegisterDto(String account, String password, String email) {

    private static final String DELIMITER = "=|&";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public static RegisterDto from(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input, DELIMITER);
        String account = "";
        String password = "";
        String email = "";

        while (tokenizer.hasMoreTokens()) {
            String key = tokenizer.nextToken();
            if (ACCOUNT.equals(key) && tokenizer.hasMoreTokens()) {
                account = tokenizer.nextToken();
                continue;
            }
            if (PASSWORD.equals(key) && tokenizer.hasMoreTokens()) {
                password = tokenizer.nextToken();
                continue;
            }
            if (EMAIL.equals(key) && tokenizer.hasMoreTokens()) {
                email = tokenizer.nextToken();
            }
        }
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            throw new IllegalArgumentException("account, password, email은 빈 값일 수 없습니다.");
        }
        return new RegisterDto(account, password, email);
    }
}
