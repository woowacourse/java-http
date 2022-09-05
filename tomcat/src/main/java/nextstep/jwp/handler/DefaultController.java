package nextstep.jwp.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatusCode;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, HttpStatusCode.OK, "text/plain", "Hello world!");
    }
}
