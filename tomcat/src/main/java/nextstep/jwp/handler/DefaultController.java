package nextstep.jwp.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatusCode;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return new HttpResponse(httpRequest, HttpStatusCode.OK, "text/plain", "Hello world!");
    }
}
