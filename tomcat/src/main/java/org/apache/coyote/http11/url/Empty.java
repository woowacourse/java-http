package org.apache.coyote.http11.url;

import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Empty extends Url {
    private static final Logger log = LoggerFactory.getLogger(Empty.class);

    public Empty(final String url) {
        super(url);
    }

    @Override
    public Http11Response getResponse() {
        log.info("path : {} ", getPath());
        throw new IllegalArgumentException("경로가 비어있습니다.");
    }
}
