package org.apache.coyote.exception;

public class UserNotFoundException extends RuntimeException {

    public static final String USER_NOT_FOUND_MESSAGE = "해당 유저가 존재하지 않습니다.";

    public UserNotFoundException() {
        super(USER_NOT_FOUND_MESSAGE);
    }
}
