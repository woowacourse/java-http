package com.techcourse.exception;

public class DuplicatedAccountException extends RuntimeException {
    public DuplicatedAccountException(String account) {
        super("account " + account + " already exist in database.");
    }
}
