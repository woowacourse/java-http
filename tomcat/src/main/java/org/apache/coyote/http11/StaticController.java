package org.apache.coyote.http11;

public class StaticController implements Controller {

    private static final String INDEX_URI = "/";

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (request.getUri().equals(INDEX_URI)) {
            request = HttpRequest.toIndex();
        }

        return new HttpResponse(StatusCode.OK, "text/" + request.getExtension(),
                ViewLoader.from(request.getUri()));
    }
}
