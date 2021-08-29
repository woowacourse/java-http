package nextstep.jwp.framework.http.template;

import nextstep.jwp.framework.http.ContentType;
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
}
