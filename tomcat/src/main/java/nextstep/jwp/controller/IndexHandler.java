package nextstep.jwp.controller;

import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.Util;

import static org.apache.coyote.model.request.ContentType.HTML;

public class IndexHandler extends AbstractHandler {

    public IndexHandler(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public String getResponse() {
        String responseBody = Util.getResponseBody(httpRequest.getPath(), getClass());
        ResponseLine responseLine = ResponseLine.of(StatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(HTML.getExtension(), responseBody, responseLine);
        return httpResponse.getResponse();
    }
}
