package org.apache.coyote.http11.handler;

import org.apache.coyote.model.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.utils.RequestUtil;

import static org.apache.coyote.model.ContentType.HTML;

public class IndexHandler implements Handler {

    private final HttpRequest httpRequest;

    public IndexHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String responseBody = RequestUtil.getResponseBody("/index.html", getClass());
        HttpResponse httpResponse = HttpResponse.of(HTML.getExtension(), responseBody);
        return httpResponse.getOkResponse();
    }
}
