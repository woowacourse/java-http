package org.apache.coyote.http11;

public class MyException extends RuntimeException {

    private final int statusCode;

    public MyException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
