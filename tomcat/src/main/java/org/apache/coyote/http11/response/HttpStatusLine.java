package org.apache.coyote.http11.response;

import common.http.HttpStatus;

import static common.Constants.SPACE;

class HttpStatusLine {

    private String versionOfTheProtocol;
    private HttpStatus httpStatus;

    HttpStatusLine() {}

    void addVersionOfTheProtocol(String versionOfTheProtocol) {
        if (this.versionOfTheProtocol != null) {
            throw new IllegalStateException("프로토콜의 버전이 이미 존재합니다.");
        }

        this.versionOfTheProtocol = versionOfTheProtocol;
    }

    void addHttpStatus(HttpStatus httpStatus) {
        if (this.httpStatus != null) {
            throw new IllegalStateException("Http Status가 이미 존재합니다.");
        }

        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return versionOfTheProtocol + SPACE + httpStatus.getStatusCode() + SPACE + httpStatus.getStatusMessage() + SPACE;
    }
}
