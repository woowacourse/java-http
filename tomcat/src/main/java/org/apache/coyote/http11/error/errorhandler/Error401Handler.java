package org.apache.coyote.http11.error.errorhandler;

public class Error401Handler implements ErrorHandler {

    @Override
    public String handleError() {
        return "/401.html";
    }
}
