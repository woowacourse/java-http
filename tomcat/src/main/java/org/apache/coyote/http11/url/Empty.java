package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Empty extends Url {
    private static final Logger log = LoggerFactory.getLogger(Empty.class);

    public Empty(final String url) {
        super(url, "GET");
    }

    @Override
    public Http11Response getResponse(HttpHeaders httpHeaders) {
        log.info("path : {} ", getPath());
        return new Http11Response(getPath(), HttpStatus.OK, "Hello world!");
    }

    @Override
    public Http11Response postResponse(HttpHeaders httpHeaders, String requestBody) {
        throw new IllegalArgumentException("EMPTY url은 POST로 요청이 들어올수 없습니다.");
    }
}
