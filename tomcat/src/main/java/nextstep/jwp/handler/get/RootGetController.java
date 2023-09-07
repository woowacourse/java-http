package nextstep.jwp.handler.get;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public class RootGetController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.HTML);
        response.setResponseBody("Hello world!");
    }
}
