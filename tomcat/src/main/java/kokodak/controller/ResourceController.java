package kokodak.controller;

import static kokodak.http.HeaderConstants.ACCEPT;

import java.net.URL;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        final String path = httpRequest.getRequestTarget().getPath();
        final String fileName = "static" + path;
        final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        if (resourceUrl == null) {
            httpResponse.notFound(httpRequest);
        }
        httpResponse.setBody(fileName, httpRequest.header(ACCEPT));
    }
}
