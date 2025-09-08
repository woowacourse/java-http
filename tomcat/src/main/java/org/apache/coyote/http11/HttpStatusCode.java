package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK(200, "OK"),
    REDIRECT(302, "Redirect")
    ;

    private final int code;
    private final String message;

    HttpStatusCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public int getCode(){
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }

}
