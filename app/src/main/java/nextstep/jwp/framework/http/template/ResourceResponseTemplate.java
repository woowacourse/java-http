package nextstep.jwp.framework.http.template;

import java.util.Arrays;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.framework.util.ResourceUtils;

public class ResourceResponseTemplate extends AbstractResponseTemplate {

    public HttpResponse ok(String path) {
        return template(HttpStatus.OK, path);
    }

    public HttpResponse unauthorized(String path) {
        return template(HttpStatus.UNAUTHORIZED, path);
    }

    private HttpResponse template(HttpStatus httpStatus, String path) {
        return template(httpStatus, new HttpHeaders(), path);
    }

    @Override
    public HttpResponse template(HttpStatus httpStatus, HttpHeaders httpHeaders, String path) {
        httpHeaders.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.resolve(path));
        final String returnValue = ResourceUtils.readString(path);
        return super.template(httpStatus, httpHeaders, returnValue);
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
