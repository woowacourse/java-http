package com.techcourse.exception;

public class DashboardException extends RuntimeException {

    public DashboardException(Exception e) {
        super(e);
    }

    public DashboardException(String message) {
        super(message);
    }
}
