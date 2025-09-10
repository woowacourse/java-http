package org.apache.coyote.http11.response.header;

import org.apache.coyote.http11.response.ResponseBody;

public class ContentLength extends ResponseHeader {

    public ContentLength(String value) {
        super("Content-Length", value);
    }

    public static ContentLength fromResponseBody(ResponseBody responseBody) {
        return new ContentLength(String.valueOf(responseBody.data().length));
    }
}
