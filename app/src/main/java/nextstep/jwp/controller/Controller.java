package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    boolean matchUri(String uri);

    HttpResponse doService(HttpRequest httpRequest) throws IOException;
}
