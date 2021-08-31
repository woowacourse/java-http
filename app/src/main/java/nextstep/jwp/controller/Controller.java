package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    HttpResponse doService(HttpRequest httpRequest) throws IOException;
}
