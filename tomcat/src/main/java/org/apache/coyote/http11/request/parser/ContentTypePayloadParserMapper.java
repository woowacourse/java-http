package org.apache.coyote.http11.request.parser;

import java.util.Arrays;

public enum ContentTypePayloadParserMapper {
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", new FormUrlEncodedPayloadParser()),
    ;

    private final String contentType;
    private final PayloadParser payloadParser;

    ContentTypePayloadParserMapper(final String contentType, final PayloadParser payloadParser) {
        this.contentType = contentType;
        this.payloadParser = payloadParser;
    }

    public static ContentTypePayloadParserMapper from(final String requestContentType) {
        return Arrays.stream(ContentTypePayloadParserMapper.values())
                .filter(contentTypePayloadParserMapper -> contentTypePayloadParserMapper.contentType.equalsIgnoreCase(
                        requestContentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Content-Type 입니다."));
    }

    public PayloadParser getPayloadParser() {
        return payloadParser;
    }
}
