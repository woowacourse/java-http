package org.apache.coyote.http11.request.model;

import java.text.MessageFormat;
import java.util.Arrays;
import org.apache.coyote.exception.UnSupportedMediaType;
import org.apache.coyote.http11.model.ContentType;

public class HttpRequestUri {

    public static final String INDEX_URI = "/";

    private final String value;

    public HttpRequestUri(final String uri) {
        this.value = uri;
    }

    public String getValue() {
        return value;
    }

    public ContentType getContentType() {
        return RequestContentType.find(value);
    }

    public static HttpRequestUri of(final String uri) {
        return new HttpRequestUri(uri);
    }

    public boolean isIndex() {
        return value.equals(INDEX_URI);
    }

    private enum RequestContentType {
        HTML(".html", ContentType.TEXT_HTML_CHARSET_UTF_8),
        CSS(".css", ContentType.TEXT_CSS_CHARSET_UTF_8),
        JS(".js", ContentType.TEXT_JS_CHARSET_UTF_8),
        ;

        private final String extension;
        private final ContentType contentType;

        RequestContentType(final String extension, final ContentType contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        public static ContentType find(final String uri) {
            RequestContentType requestContentType = Arrays.stream(RequestContentType.values())
                    .filter(it -> uri.endsWith(it.extension))
                    .findFirst()
                    .orElseThrow(() -> new UnSupportedMediaType(MessageFormat.format("not found type : {0}", uri)));
            return requestContentType.contentType;
        }
    }
}
