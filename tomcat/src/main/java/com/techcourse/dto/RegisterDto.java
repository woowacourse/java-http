package com.techcourse.dto;

import java.util.Map;

public record RegisterDto(String account, String password, String email) {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public static RegisterDto from(Map<String, String> queries) {
        validate(queries);
        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);
        String email = queries.get(EMAIL);

        return new RegisterDto(account, password, email);
    }

    private static void validate(Map<String, String> queries) {
        if (!queries.containsKey(ACCOUNT) || !queries.containsKey(PASSWORD) || !queries.containsKey(EMAIL)) {
            throw new IllegalArgumentException("account, password, email 값이 유효하지 않습니다.");
        }
    }
}
