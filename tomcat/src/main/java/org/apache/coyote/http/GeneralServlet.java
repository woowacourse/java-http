package org.apache.coyote.http;

public class GeneralServlet implements Servlet {

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
