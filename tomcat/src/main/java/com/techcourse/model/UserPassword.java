package com.techcourse.model;

import org.apache.commons.lang3.StringUtils;

public record UserPassword(String password) {

    public UserPassword {
        validatePassword(password);
    }

    private void validatePassword(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("비밀번호는 공백일 수 없습니다.");
        }
    }
}
