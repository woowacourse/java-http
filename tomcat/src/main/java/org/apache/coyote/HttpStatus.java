package org.apache.coyote;

public class HttpStatus {

    public static final HttpStatus OK = new HttpStatus("200", "OK");
    public static final HttpStatus FOUND = new HttpStatus("302", "Found");

    private String code;
    private String message;

    public HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
