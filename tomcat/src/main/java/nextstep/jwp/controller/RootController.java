package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.OK;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;

public class RootController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        doGet(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String responseBody = "Hello world!";
        response.statusCode(OK)
                .addHeader(CONTENT_TYPE, ContentType.of(request.getFileExtension()).getValue())
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length)
                .responseBody(responseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new MethodNotAllowedException();
    }
}
