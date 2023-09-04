package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.http11.exception.Http11Exception;

/**
 * HTTP-Version   = "HTTP" "/" 1*DIGIT "." 1*DIGIT
 * <major>.<minor>
 */
public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    ;

    private final String field;

    HttpVersion(final String field) {
        this.field = field;
    }

    public static HttpVersion from(final String field) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.field.equals(field))
                .findAny()
                .orElseThrow(() -> new Http11Exception("입력한 Http Version을 지원할 수 없습니다."));
    }

    public String getField() {
        return field;
    }
}
