package org.apache.coyote.httprequest.exception;

public class InvalidCacheControlHeader extends IllegalArgumentException {

    public InvalidCacheControlHeader() {
        super("잘못된 Cache-Control 헤더 입니다.");
    }
}
