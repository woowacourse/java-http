package org.apache.coyote.http11.handler;

import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.Util;

import static org.apache.coyote.model.request.ContentType.HTML;

public class IndexHandler implements Handler {

    private static final String INDEX_HTML = "/index.html";
    private final HttpRequest httpRequest;

    public IndexHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String responseBody = Util.getResponseBody(INDEX_HTML, getClass());
        ResponseLine responseLine = ResponseLine.of(StatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(HTML.getExtension(), responseBody, responseLine);
        return httpResponse.getResponse();
    }
}
