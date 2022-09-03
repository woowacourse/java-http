package nextstep.jwp.controller;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
