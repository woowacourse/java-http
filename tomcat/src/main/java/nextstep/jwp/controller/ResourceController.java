package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Controller {


    @Override
    public HttpResponse process(HttpRequest httpRequest) throws Exception {
        String url = httpRequest.getUrl();
        return HttpResponse.ok(url);
    }
}
