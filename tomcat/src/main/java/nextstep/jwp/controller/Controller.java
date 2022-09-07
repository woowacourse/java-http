package nextstep.jwp.controller;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest httpRequest);
}
