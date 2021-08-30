package nextstep.jwp.controller;

import java.io.IOException;
import java.io.InputStream;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;

public class RequestController extends AbstractController {

    private HttpRequest request;

    public void run(final InputStream inputStream) throws IOException {
        request = new HttpRequest(inputStream);
    }

    public HttpResponse toResponse() {
        return request.toHttpResponse();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        super.doGet(request, response);
    }
}
