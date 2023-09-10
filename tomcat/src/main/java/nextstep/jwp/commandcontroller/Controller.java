package nextstep.jwp.commandcontroller;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest httpRequest);
    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
