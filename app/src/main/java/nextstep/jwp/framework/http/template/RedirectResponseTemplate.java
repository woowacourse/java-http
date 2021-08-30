package nextstep.jwp.framework.http.template;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

public class RedirectResponseTemplate extends AbstractResponseTemplate {

    private static final String EMPTY_BODY = "";

    public HttpResponse found(String location) {
        return found(location, new HttpHeaders());
    }

    public HttpResponse found(String location, HttpHeaders httpHeaders) {
        httpHeaders.addHeader(HttpHeaders.LOCATION, location);
        return template(HttpStatus.FOUND, httpHeaders, EMPTY_BODY);
    }
}
