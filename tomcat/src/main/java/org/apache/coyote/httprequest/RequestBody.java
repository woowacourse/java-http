package org.apache.coyote.httprequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBody {

    private static final Logger log = LoggerFactory.getLogger(RequestBody.class);

    private final String contents;

    public RequestBody(final String contents) {
        log.debug("Request body: {}", contents);
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }
}
