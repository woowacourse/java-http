package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Controller {

    void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;

}
