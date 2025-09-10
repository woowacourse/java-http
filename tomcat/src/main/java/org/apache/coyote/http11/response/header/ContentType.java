package org.apache.coyote.http11.response.header;

import org.apache.coyote.MimeType;
import org.apache.coyote.http11.response.ResponseBody;

public class ContentType extends ResponseHeader {

    public ContentType(String value) {
        super("Content-Type", value);
    }

    public static ContentType fromMimeType(MimeType mimeType) {
        return new ContentType(mimeType.getName());
    }

    public static ContentType fromResponseBody(ResponseBody responseBody) {
        return fromMimeType(responseBody.mimeType());
    }
}
