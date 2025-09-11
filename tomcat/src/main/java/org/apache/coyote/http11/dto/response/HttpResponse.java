package org.apache.coyote.http11.dto.response;

import static org.apache.coyote.http11.HttpConstants.CRLF;

import java.nio.charset.StandardCharsets;

public record HttpResponse(
        ResponseLine responseLine,
        ResponseHeader responseHeader,
        ResponseBody responseBody
) {

    public byte[] toBytes() {
        final String sb = responseLine.toString() + CRLF
                + responseHeader.toString() + CRLF
                + responseBody.toString();

        return sb.getBytes(StandardCharsets.UTF_8);
    }
}
