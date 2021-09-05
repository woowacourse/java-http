package nextstep.jwp.mvc.controller;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    String INDEX_HTML = "/index.html";
    String ERROR_401_HTML = "/401.html";
    String ERROR_404_HTML = "/404.html";
    String ERROR_500_HTML = "/500.html";

    HttpResponse service(HttpRequest request) throws IOException;
}
