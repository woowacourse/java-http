package com.techcourse.model;

import org.apache.commons.lang3.StringUtils;

public record UserEmail(String email) {

    public UserEmail {
        validateEmail(email);
    }

    private void validateEmail(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("이메일은 공백일 수 없습니다.");
        }
    }
}
