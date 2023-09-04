package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200,"OK"),
    FOUND(302,"Found"),
    UNAUTHORIZED(401,"Unauthorized"),
    NOTFOUND(404,"Not Found");

    private final int value;
    private final String detail;

    HttpStatus(int value, String detail) {
        this.value = value;
        this.detail = detail;
    }

    public int getValue() {
        return value;
    }

    public String getDetail() {
        return detail;
    }



    @Override
    public String toString() {
        return value + " " + detail;
    }
}
