package nextstep.jwp.framework.http.template;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

public abstract class AbstractResponseTemplate implements ResponseTemplate {

    @Override
    public HttpResponse template(HttpStatus status, HttpHeaders httpHeaders, String returnValue) {
        final HttpResponse.Builder builder = HttpResponse.status(status);

        builder.httpHeaders(httpHeaders);

        return builder.body(returnValue).build();
    }
}
