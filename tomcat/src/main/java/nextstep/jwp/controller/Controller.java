package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    HttpResponse doService(HttpRequest request);

}
