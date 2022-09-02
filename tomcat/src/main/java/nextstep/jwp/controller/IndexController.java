package nextstep.jwp.controller;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController implements Controller {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().equals(HttpMethod.GET)) {
            return show();
        }
        return HttpResponse.found("/404.html");
    }

    public HttpResponse show() {
        return HttpResponse.ok(new StaticResource("Hello world!", ContentType.TEXT_HTML));
    }
}
