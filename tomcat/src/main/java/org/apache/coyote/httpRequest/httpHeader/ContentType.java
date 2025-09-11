package org.apache.coyote.httpRequest.httpHeader;

import java.util.Arrays;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;

public enum ContentType {

    APPLICATION_JSON("application/json"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded");

    private final String contentType;

    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static ContentType findContentType(final String input) {
        return Arrays.stream(ContentType.values())
                .filter(type -> input.contains(type.contentType))
                .findFirst()
                .orElseThrow(() -> new HttpException(ErrorCode.NOT_ALLOW_MEDIA_TYPE));
    }
}
