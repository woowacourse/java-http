package org.apache.coyote.http11;

import static org.apache.coyote.http11.Http11Request.PATH_INDEX;
import static org.apache.coyote.http11.Http11Request.QUERY_PARAM_DELIMITER_REGEX;

public class Http11URL {

    private final String url;

    public Http11URL(final String url) {
        this.url = url;
    }

    public static Http11URL of(final String uri) {
        return new Http11URL(uri.split(QUERY_PARAM_DELIMITER_REGEX)[PATH_INDEX]);
    }
}
