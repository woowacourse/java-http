package nextstep.jwp.controller;

import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.model.request.ContentType.HTML;

public class IndexHandler extends AbstractHandler {

    private static final IndexHandler INSTANCE = new IndexHandler();

    private IndexHandler() {
    }

    public static IndexHandler getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public String getResponse(final HttpRequest httpRequest) {
        String responseBody = HttpResponse.getResponseBody(httpRequest.getPath(), getClass());
        ResponseLine responseLine = ResponseLine.of(StatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(HTML.getExtension(), responseBody, responseLine);
        return httpResponse.getResponse();
    }
}
