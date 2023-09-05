package org.apache.coyote.http11;

public class ContentType {

    public static final ContentType PLAINTEXT_UTF8 = new ContentType("text/plain", "utf-8");


    private final String mimeType;
    private final String charset;

    public ContentType(final String mimeType) {
        this(mimeType, "utf-8");
    }

    public ContentType(final String mimeType, final String charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "Content-Type: " + mimeType + ";charset=" + charset + " ";
    }

}
