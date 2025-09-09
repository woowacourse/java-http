package org.apache.coyote.http11.vo;

public record HttpResponse(
        String mediaType,
        HttpStatus status,
        String body
) {

    public int getStatusCode() {
        return status.value();
    }

    public String getStatusReason() {
        return status.reason();
    }
}
