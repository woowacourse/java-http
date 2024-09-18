package com.techcourse.exception;

public class DuplicatedAccountException extends RuntimeException {
    public DuplicatedAccountException(String account) {
        throw new RuntimeException("account " + account + " already exist in database.");
    }
}
