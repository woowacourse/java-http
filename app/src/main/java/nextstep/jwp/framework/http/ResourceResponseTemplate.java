package nextstep.jwp.framework.http;

import java.util.Arrays;

import nextstep.jwp.framework.util.ResourceUtils;

public class ResourceResponseTemplate implements ResponseTemplate {

    public HttpResponse ok(String path) {
        return template(HttpStatus.OK, path);
    }

    public HttpResponse unauthorized(String path) {
        return template(HttpStatus.UNAUTHORIZED, path);
    }

    public HttpResponse movedPermanently(String path) {
        return template(HttpStatus.MOVED_PERMANENTLY, path, new HttpHeader(HttpHeader.LOCATION, path));
    }

    public HttpResponse found(String path) {
        return template(HttpStatus.FOUND, path, new HttpHeader(HttpHeader.LOCATION, path));
    }

    public HttpResponse seeOther(String path) {
        return template(HttpStatus.SEE_OTHER, path, new HttpHeader(HttpHeader.LOCATION, path));
    }

    public HttpResponse temporaryRedirect(String path) {
        return template(HttpStatus.TEMPORARY_REDIRECT, path, new HttpHeader(HttpHeader.LOCATION, path));
    }

    private HttpResponse template(HttpStatus httpStatus, String path, HttpHeader... headers) {
        final HttpHeaders httpHeaders =
                new HttpHeaders().addHeader(HttpHeader.CONTENT_TYPE, ContentType.resolve(path));
        for (HttpHeader header : headers) {
            httpHeaders.addHeader(header);
        }

        final String returnValue = ResourceUtils.readString(path);
        return template(httpStatus, httpHeaders, returnValue);
    }

    enum ContentType {
        PLAIN("text/plain"), HTML("text/html"), CSS("text/css");

        public static final String UTF_8 = ";charset=utf-8";

        private final String name;

        ContentType(String name) {
            this.name = name;
        }

        public String getType() {
            return name;
        }

        public boolean isSameExtension(String extension) {
            return this.name().toLowerCase().equals(extension);
        }

        private static String resolve(String path) {
            final String extension = ResourceUtils.getFileExtension(path);

            return Arrays.stream(values())
                         .filter(contentType -> contentType.isSameExtension(extension))
                         .findAny()
                         .orElse(PLAIN)
                         .getType() + UTF_8;
        }
    }
}
