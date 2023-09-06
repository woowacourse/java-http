package org.apache.coyote.http.request;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http.MediaType;

public class ContentType {

    private static final String MEDIA_TYPE_CHARSET_DELIMITER = ";charset=";

    private MediaType mediaType;
    private Charset charset;

    private ContentType(MediaType mediaType, Charset charset) {
        this.mediaType = mediaType;
        this.charset = charset;
    }

    private ContentType(MediaType mediaType) {
        this(mediaType, null);
    }

    public static ContentType of(MediaType mediaType, Charset charset) {
        return new ContentType(mediaType, charset);
    }

    public static ContentType of(String contentTypeString) {
        String[] split = contentTypeString.split(MEDIA_TYPE_CHARSET_DELIMITER);
        MediaType mediaType = MediaType.fromValue(split[0]);
        if (1 < split.length) {
            return new ContentType(mediaType, StandardCharsets.UTF_8);
        }
        return new ContentType(mediaType);
    }

    public boolean isFormUrlEncoded() {
        return mediaType == MediaType.APPLICATION_X_WWW_FORM_URL_ENCODED;
    }

    public String getValue() {
        if (charset != null) {
            return mediaType.value + MEDIA_TYPE_CHARSET_DELIMITER + charset.name().toLowerCase();
        }
        return mediaType.value;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}
