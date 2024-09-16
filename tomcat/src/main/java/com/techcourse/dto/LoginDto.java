package com.techcourse.dto;

import java.util.StringTokenizer;

public record LoginDto(String account, String password) {

    private static final String DELIMITER = "=|&";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static LoginDto from(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input, DELIMITER);
        String account = "";
        String password = "";

        while (tokenizer.hasMoreTokens()) {
            String key = tokenizer.nextToken();
            if (ACCOUNT.equals(key) && tokenizer.hasMoreTokens()) {
                account = tokenizer.nextToken();
                continue;
            }
            if (PASSWORD.equals(key) && tokenizer.hasMoreTokens()) {
                password = tokenizer.nextToken();
            }
        }
        if (account.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("account, password는 빈 값일 수 없습니다.");
        }
        return new LoginDto(account, password);
    }
}
