package org.apache.coyote.http11.request;

import org.apache.coyote.http11.response.MimeType;

public class Resource {

    private MimeType mimeType;
    private String data;

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
