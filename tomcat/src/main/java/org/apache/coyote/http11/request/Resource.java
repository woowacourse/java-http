package org.apache.coyote.http11.request;

import org.apache.coyote.http11.response.spec.MimeType;

public class Resource {

    private final MimeType mimeType;
    private final String data;

    public Resource(MimeType mimeType, String data) {
        this.mimeType = mimeType;
        this.data = data;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public String getData() {
        return data;
    }
}
