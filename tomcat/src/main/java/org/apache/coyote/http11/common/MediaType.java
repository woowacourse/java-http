package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum MediaType {

    ALL("*", "*"),
    APPLICATION_FORM_URLENCODED("application", "x-www-form-urlencoded"),
    APPLICATION_JSON("application", "json"),
    APPLICATION_OCTET_STREAM("application", "octet-stream"),
    APPLICATION_PDF("application", "pdf"),
    APPLICATION_XML("application", "xml"),
    IMAGE_GIF("image", "gif"),
    IMAGE_JPEG("image", "jpeg"),
    IMAGE_PNG("image", "png"),
    MULTIPART_FORM_DATA("multipart", "form-data"),
    MULTIPART_MIXED("multipart", "mixed"),
    MULTIPART_RELATED("multipart", "related"),
    TEXT_HTML("text", "html"),
    TEXT_MARKDOWN("text", "markdown"),
    TEXT_PLAIN("text", "plain"),
    TEXT_XML("text", "xml"),
    TEXT_JAVASCRIPT("text", "javascript"),
    TEXT_CSS("text", "css");

    private final String type;
    private final String subType;

    MediaType(final String type, final String subType) {
        this.type = type;
        this.subType = subType;
    }

    public static MediaType getMediaType(final String mediaType) {
        return Arrays.stream(values())
                .filter(mediaType1 -> mediaType1.stringify().equals(mediaType))
                .findFirst()
                .orElse(null);
    }

    public static MediaType getMediaTypeByFileExtension(final String fileExtension) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.getSubType().equals(fileExtension))
                .findFirst()
                .orElse(null);
    }

    public static boolean isSupported(final MediaType mediaType) {
        return mediaType != null && !mediaType.isWildCard(mediaType);
    }

    private boolean isWildCard(final MediaType mediaType) {
        return mediaType.equals(ALL);
    }

    public String stringify() {
        return type + "/" + subType + ";" + "charset=utf-8";
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }
}
