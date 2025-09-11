package org.apache.coyote.http11;

public class HomeHttpRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET && httpRequest.getRequestUrl()
                .equals("/");
    }

    @Override
    public void response(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        httpResponse.ok()
                .write("Hello world!");
    }
}
