package org.apache.coyote.http11;

import org.apache.coyote.http11.general.ContentType;

public class Resource {

    private final ContentType contentType;
    private final String data;

    public Resource(ContentType contentType, String data) {
        this.contentType = contentType;
        this.data = data;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getData() {
        return data;
    }
}
