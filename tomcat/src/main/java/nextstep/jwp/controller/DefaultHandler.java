package nextstep.jwp.controller;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.model.request.RequestLine.getExtension;

public class DefaultHandler extends AbstractHandler {

    private static final DefaultHandler INSTANCE = new DefaultHandler();

    private DefaultHandler() {
    }

    public static DefaultHandler getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public String getResponse(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        final String extension = ContentType.getType(getExtension(path));
        final String body = HttpResponse.getResponseBody(path, this.getClass());

        return HttpResponse.of(extension, body, ResponseLine.of(StatusCode.OK))
                .getResponse();
    }
}
