package nextstep.jwp.handler.get;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class RootGetHandler implements Handler {

    @Override
    public Http11Response resolve(final Http11Request request) {
        return new Http11Response(StatusCode.OK, ContentType.HTML, "Hello world!");
    }
}
