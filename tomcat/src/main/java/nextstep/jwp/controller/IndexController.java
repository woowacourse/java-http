package nextstep.jwp.controller;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class IndexController extends AbstractController {

    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.ok(new StaticResource("Hello world!", ContentType.TEXT_HTML));
    }
}
