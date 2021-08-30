package nextstep.jwp.framework.http.template;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;

public interface ResponseTemplate {
    HttpResponse template(HttpStatus status, HttpHeaders httpHeaders, String returnValue);
}
