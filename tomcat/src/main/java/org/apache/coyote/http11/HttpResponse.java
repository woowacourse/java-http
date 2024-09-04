package org.apache.coyote.http11;

public record HttpResponse(String value) {

    public byte[] getBytes() {
        return value.getBytes();
    }
}
