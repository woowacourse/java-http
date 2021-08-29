package nextstep.jwp.controller;


import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    void process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
