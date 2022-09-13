package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public interface Controller {

    HttpResponse service(HttpRequest request, HttpResponseBuilder responseBuilder) throws Exception;
}
