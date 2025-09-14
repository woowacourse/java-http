package org.apache.coyote.http11;

public class StaticResource {

    private final String mimeType;
    private final byte[] content;

    public StaticResource(final String mimeType, final byte[] content) {
        this.mimeType = mimeType;
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getContent() {
        if (isEmpty()) {
            return new byte[0];
        }

        return content;
    }

    public int getContentLength() {
        if (isEmpty()) {
            return 0;
        }

        return content.length;
    }

    private boolean isEmpty() {
        return (content == null) || (content.length == 0);
    }
}
