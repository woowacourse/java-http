package org.apache.coyote.request;

public class MalformedRequestException extends IllegalArgumentException {

    public MalformedRequestException(String message) {
        super(message);
    }
}
