package org.apache.catalina.domain;

import com.http.enums.HttpStatus;

public record ResponseStartLine(String version, HttpStatus httpStatus) {

    @Override
    public String toString() {
        return version + " " + httpStatus.getCode() + " " + httpStatus.getReasonPhrase();
    }
}
