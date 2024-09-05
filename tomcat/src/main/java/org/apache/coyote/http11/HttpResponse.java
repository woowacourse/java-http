package org.apache.coyote.http11;

public record HttpResponse(byte[] responseBody, String contentType) {
}
