package com.techcourse.exception;

public class UnknownAccountException extends RuntimeException {
    public UnknownAccountException(String account) {
        throw new RuntimeException("account " + account + " does not exist in database.");
    }
}
