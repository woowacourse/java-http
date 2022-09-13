package org.apache.coyote.exception;

public class VersionNotFoundException extends InternalServerException {

    public VersionNotFoundException() {
        super("HTTP 버전을 찾지 못했습니디.");
    }
}
