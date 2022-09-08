package nextstep.jwp.controller;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

@FunctionalInterface
public interface Controller {

    HttpResponse service(HttpRequest request);
}
