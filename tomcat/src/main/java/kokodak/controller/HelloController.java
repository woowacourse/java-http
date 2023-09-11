package kokodak.controller;

import kokodak.RequestMapper;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public class HelloController extends AbstractController {

    static {
        RequestMapper.register("/", new HelloController());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        httpResponse.setBody("Hello world!");
    }
}
