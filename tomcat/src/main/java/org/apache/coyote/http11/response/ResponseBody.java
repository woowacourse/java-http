package org.apache.coyote.http11.response;

import org.apache.coyote.MimeType;
import org.apache.coyote.TextResource;

import java.nio.charset.StandardCharsets;

public record ResponseBody(
        byte[] data,
        MimeType mimeType
) {

    public static ResponseBody fromTextResource(TextResource textResource) {
        return new ResponseBody(
                textResource.content().getBytes(StandardCharsets.UTF_8),
                MimeType.fromResource(textResource)
        );
    }
}
