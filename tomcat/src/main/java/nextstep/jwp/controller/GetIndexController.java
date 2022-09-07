package nextstep.jwp.controller;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Controller;

public class GetIndexController implements Controller {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath("/index.html");
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();

        return path.equals("/index.html") || path.equals("/index");
    }
}
