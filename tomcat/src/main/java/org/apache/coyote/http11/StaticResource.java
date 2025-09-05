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
            return "Hello world!".getBytes();
        }
        return content;
    }

    public int getContentLength() {
        if (isEmpty()) {
            return "Hello world!".getBytes().length;
        }
        return new String(content).replaceAll("\r\n", "\n").getBytes().length;
    }

    private boolean isEmpty() {
        return content == null || content.length == 0;
    }
}
