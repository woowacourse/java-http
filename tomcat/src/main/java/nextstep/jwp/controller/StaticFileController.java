package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static util.FileLoader.loadFile;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String responseBody = loadFile(request.getUrl());
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
