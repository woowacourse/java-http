package com.techcourse.model;

import org.apache.commons.lang3.StringUtils;

public record UserAccount(String account) {

    public UserAccount {
        validateAccount(account);
    }

    private void validateAccount(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("유저명은 공백일 수 없습니다.");
        }
    }
}
