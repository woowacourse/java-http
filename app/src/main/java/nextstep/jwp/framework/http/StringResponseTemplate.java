package nextstep.jwp.framework.http;

public class StringResponseTemplate implements ResponseTemplate {

    private static final String PLAIN_CONTENT_TYPE = "text/plain;charset=utf-8";

    private static final String EMPTY_BODY = "";

    public HttpResponse ok(String body) {
        final HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeader.CONTENT_TYPE, PLAIN_CONTENT_TYPE);
        return template(HttpStatus.OK, httpHeaders, body);
    }

    public HttpResponse found(String location) {
        final HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeader.LOCATION, location);
        return template(HttpStatus.FOUND, httpHeaders, EMPTY_BODY);
    }
}
