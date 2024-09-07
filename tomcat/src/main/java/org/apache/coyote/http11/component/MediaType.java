package org.apache.coyote.http11.component;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum MediaType {

    ALL("*/*"),
    APPLICATION_ATOM_XML("application/atom+xml"),
    APPLICATION_CBOR("application/cbor"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_GRAPHQL("application/graphql+json"),
    APPLICATION_GRAPHQL_RESPONSE("application/graphql-response+json"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_JSON("application/json"),
    APPLICATION_JSON_UTF8("application/json"),
    APPLICATION_NDJSON("application/x-ndjson"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_PROBLEM_JSON("application/problem+json"),
    APPLICATION_PROBLEM_JSON_UTF8("application/problem+json"),
    APPLICATION_PROBLEM_XML("application/problem+xml"),
    APPLICATION_PROTOBUF("application/x-protobuf"),
    APPLICATION_RSS_XML("application/rss+xml"),
    APPLICATION_STREAM_JSON("application/stream+json"),
    APPLICATION_XHTML_XML("application/xhtml+xml"),
    APPLICATION_XML("application/xml"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    MULTIPART_MIXED("multipart/mixed"),
    MULTIPART_RELATED("multipart/related"),
    TEXT_EVENT_STREAM("text/event-stream"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_MARKDOWN("text/markdown"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public static MediaType from(String value) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new UncheckedServletException(new IllegalArgumentException("정상적인 동작이 아닙니다.")));
    }

    public String getValue() {
        if (isHtml()) {
            return value + ";charset=utf-8";
        }
        return value;
    }

    private boolean isHtml() {
        return TEXT_HTML.equals(this);
    }
}
