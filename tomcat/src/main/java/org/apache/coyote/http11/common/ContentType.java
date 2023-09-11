package org.apache.coyote.http11.common;

public class ContentType {

    private static final String MEDIA_TYPE_CHARSET_DELIMITER = ";";

    private final MediaType mediaType;
    private final Charset charset;

    private ContentType(MediaType mediaType, Charset charset) {
        this.mediaType = mediaType;
        this.charset = charset;
    }

    public static ContentType of(MediaType mediaType, Charset charset) {
        return new ContentType(mediaType, charset);
    }

    public String format() {
        return mediaType.getValue() + MEDIA_TYPE_CHARSET_DELIMITER + charset.format();
    }
}
