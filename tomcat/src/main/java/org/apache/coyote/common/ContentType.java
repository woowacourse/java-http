package org.apache.coyote.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.coyote.request.Accept;

public enum ContentType {

    APPLICATION_XML("application/xml", "xml"),
    APPLICATION_JAVASCRIPT("application/javascript", "js"),
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_OCTET_STREAM("application/octet-stream", ""),
    APPLICATION_PDF("application/pdf", "pdf"),
    TEXT_PLAIN("text/plain", "text"),
    TEXT_HTML("text/html;charset=utf-8", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JAVASCRIPT("text/javascript", "js"),
    TEXT_XML("text/xml", "xml"),
    IMAGE_JPEG("image/jpeg", "jpeg"),
    IMAGE_PNG("image/png", "png"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_SVG("image/svg+xml", "svg"),
    AUDIO_MPEG("audio/mpeg", "mpeg"),
    AUDIO_OGG("audio/ogg", "ogg"),
    VIDEO_MP4("video/mp4", "mp4"),
    MULTIPART_FOR_DATA("multipart/form-data", "");

    private final String type;
    private final String extension;

    ContentType(final String type, final String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static String getTypeFrom(final List<Accept> accepts) {
        final Set<String> contentTypes = Arrays.stream(ContentType.values())
                .map(contentType -> contentType.type)
                .collect(Collectors.toSet());
        return accepts.stream()
                .map(accept -> accept.getAcceptType())
                .filter(accept -> contentTypes.contains(accept))
                .findFirst()
                .orElse(null);
    }

    public static String getTypeFrom(final String extension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> Objects.equals(contentType.extension, extension))
                .findAny()
                .map(contentType -> contentType.type)
                .orElse(null);
    }

    public String getType() {
        return type;
    }
}
