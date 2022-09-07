package org.apache.coyote.http;

public class GeneralController implements Controller {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath(httpRequest.getPath());
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        return false;
    }
}
