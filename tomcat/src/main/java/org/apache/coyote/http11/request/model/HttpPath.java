package org.apache.coyote.http11.request.model;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import org.apache.coyote.exception.UnSupportedMediaType;
import org.apache.coyote.http11.model.ContentType;

public class HttpPath {

    private static final String QUERY_PARAMETER = "?";

    private final String value;

    public HttpPath(final String uri) {
        this.value = uri;
    }

    public String getValue() {
        return value;
    }

    public ContentType getContentType() {
        return RequestContentType.find(value);
    }

    public static HttpPath of(final String uri) {
        return new HttpPath(uri);
    }

    public boolean isQuery() {
        return value.contains(QUERY_PARAMETER);
    }

    public boolean isBasicContentType() {
        return RequestContentType.isExist(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpPath)) {
            return false;
        }
        HttpPath that = (HttpPath) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
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

        public static boolean isExist(final String uri) {
            return Arrays.stream(RequestContentType.values())
                    .anyMatch(it -> uri.endsWith(it.extension));
        }
    }
}
