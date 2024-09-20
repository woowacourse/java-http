package com.techcourse.dto;

import java.util.Map;

public record LoginDto(String account, String password) {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static LoginDto from(Map<String, String> queries) {
        validate(queries);
        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);

        return new LoginDto(account, password);
    }

    private static void validate(Map<String, String> queries) {
        if (!queries.containsKey(ACCOUNT) || !queries.containsKey(PASSWORD)) {
            throw new IllegalArgumentException("account 혹은 password 값이 유효하지 않습니다.");
        }
    }
}
