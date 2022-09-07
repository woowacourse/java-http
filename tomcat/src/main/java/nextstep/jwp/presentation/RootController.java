package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RootController implements Controller {

    @Override
    public HttpResponse process(final HttpRequest httpRequest) {
        return new HttpResponse(
                StatusCode.OK,
                ContentType.from(httpRequest.getRequestURL().getPath()),
                "Hello world!");
    }
}
