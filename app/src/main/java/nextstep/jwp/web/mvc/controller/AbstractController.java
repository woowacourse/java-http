package nextstep.jwp.web.mvc.controller;

import nextstep.jwp.web.http.request.HttpMethod;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod method = HttpMethod.findByName(request.method().name());
        switch (method){
            case GET:
                doGet(request, response); break;
            case POST:
                doPost(request, response); break;
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response)
        throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
