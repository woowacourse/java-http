package org.apache.coyote.http11.handler;

import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.HttpStatusCode;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.RequestUtil;

import static org.apache.coyote.model.request.ContentType.HTML;

public class IndexHandler implements Handler {

    public static final String INDEX_HTML = "/index.html";
    private final HttpRequest httpRequest;

    public IndexHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String responseBody = RequestUtil.getResponseBody(INDEX_HTML, getClass());
        HttpStatusCode httpStatusCode = HttpStatusCode.of(StatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(HTML.getExtension(), responseBody, httpStatusCode);
        return httpResponse.getResponse();
    }
}
