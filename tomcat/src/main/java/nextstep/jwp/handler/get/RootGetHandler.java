package nextstep.jwp.handler.get;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootGetHandler implements Handler {

    @Override
    public HttpResponse resolve(final HttpRequest request) {
        return new HttpResponse(request.getVersion(), StatusCode.OK, ContentType.HTML, "Hello world!");
    }
}
