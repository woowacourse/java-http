package org.apache.coyote.http;

public class StaticResourceBody implements ResponseBody {

    private final byte[] content;
    private final MimeType mimeType;

    public StaticResourceBody(byte[] content, MimeType mimeType) {
        this.content = content;
        this.mimeType = mimeType;
    }

    @Override
    public String contentType() {
        return mimeType.value();
    }

    @Override
    public byte[] bytes() {
        return content;
    }
}
