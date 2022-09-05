package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Controller {


    @Override
    public HttpResponse process(HttpRequest httpRequest) throws Exception {
        String url = httpRequest.getPath().getUrl();
        return HttpResponse.ok(url);
    }
}
