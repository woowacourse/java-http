package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum SupportPayloadContentType {
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", new FormUrlEncodedPayloadParser()),
    ;

    private final String contentType;
    private final PayloadParser payloadParser;

    SupportPayloadContentType(final String contentType, final PayloadParser payloadParser) {
        this.contentType = contentType;
        this.payloadParser = payloadParser;
    }

    public static SupportPayloadContentType from(final String requestContentType) {
        return Arrays.stream(SupportPayloadContentType.values())
                .filter(supportPayloadContentType -> supportPayloadContentType.contentType.equalsIgnoreCase(
                        requestContentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Content-Type 입니다."));
    }

    public PayloadParser getPayloadParser() {
        return payloadParser;
    }
}
