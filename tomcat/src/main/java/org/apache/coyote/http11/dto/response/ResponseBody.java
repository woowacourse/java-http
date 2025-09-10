package org.apache.coyote.http11.dto.response;

public record ResponseBody(
        String body
) {

    @Override
    public String toString() {
        return body;
    }
}
