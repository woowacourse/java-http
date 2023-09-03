package org.apache.coyote.http11.exception;

public class NoSuchApiException extends RuntimeException{
    public NoSuchApiException(String message){
        super(message);
    }
}
