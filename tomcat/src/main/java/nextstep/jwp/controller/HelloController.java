package nextstep.jwp.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setBody("Hello world!", ContentType.findResponseContentTypeFromRequest(httpRequest));
        httpResponse.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }
}
