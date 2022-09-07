package org.apache.coyote.util;

import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.coyote.exception.UnSupportedMediaType;
import org.apache.coyote.http11.http.ContentType;

public enum RequestContentTypeUtils {
    HTML(".html", ContentType.TEXT_HTML_CHARSET_UTF_8),
    CSS(".css", ContentType.TEXT_CSS_CHARSET_UTF_8),
    JS(".js", ContentType.TEXT_JS_CHARSET_UTF_8),
    ;

    private final String extension;
    private final ContentType contentType;

    RequestContentTypeUtils(final String extension, final ContentType contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType find(final String uri) {
        RequestContentTypeUtils requestContentTypeUtils = Arrays.stream(RequestContentTypeUtils.values())
                .filter(it -> uri.endsWith(it.extension))
                .findFirst()
                .orElseThrow(() -> new UnSupportedMediaType(MessageFormat.format("not found type : {0}", uri)));
        return requestContentTypeUtils.contentType;
    }

    public static boolean isExist(final String uri) {
        return Arrays.stream(RequestContentTypeUtils.values())
                .anyMatch(it -> uri.endsWith(it.extension));
    }
}