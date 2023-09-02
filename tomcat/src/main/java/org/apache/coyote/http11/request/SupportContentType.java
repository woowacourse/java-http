package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum SupportContentType {
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", new FormUrlEncodedPayloadParser()),
    ;

    private final String contentType;
    private final PayloadParser payloadParser;

    SupportContentType(final String contentType, final PayloadParser payloadParser) {
        this.contentType = contentType;
        this.payloadParser = payloadParser;
    }

    public static SupportContentType from(final String requestContentType) {
        return Arrays.stream(SupportContentType.values())
                .filter(supportContentType -> supportContentType.contentType.equalsIgnoreCase(requestContentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Content-Type 입니다."));
    }

    public PayloadParser getPayloadParser() {
        return payloadParser;
    }
}
