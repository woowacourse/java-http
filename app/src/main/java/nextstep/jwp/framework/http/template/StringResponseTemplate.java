package nextstep.jwp.framework.http.template;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

public class StringResponseTemplate extends AbstractResponseTemplate {

    private static final String PLAIN_CONTENT_TYPE = "text/plain;charset=utf-8";

    private static final String EMPTY_BODY = "";

    public HttpResponse ok(String body) {
        return template(HttpStatus.OK, new HttpHeaders(), body);
    }

    public HttpResponse found(String location) {
        final HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeaders.LOCATION, location);
        return template(HttpStatus.FOUND, httpHeaders, EMPTY_BODY);
    }

    @Override
    public HttpResponse template(HttpStatus status, HttpHeaders httpHeaders, String returnValue) {
        httpHeaders.addHeader(HttpHeaders.CONTENT_TYPE, PLAIN_CONTENT_TYPE);
        return super.template(status, httpHeaders, returnValue);
    }
}
