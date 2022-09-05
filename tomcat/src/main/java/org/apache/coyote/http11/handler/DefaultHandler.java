package org.apache.coyote.http11.handler;

import org.apache.coyote.model.request.ContentType;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.HttpStatusCode;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.RequestUtil;

import static org.apache.coyote.utils.RequestUtil.getExtension;

public class DefaultHandler implements Handler {

    private final HttpRequest httpRequest;

    public DefaultHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String path = httpRequest.getPath();
        String extension = ContentType.getType(getExtension(path));
        String body = RequestUtil.getResponseBody(path, this.getClass());

        return HttpResponse.of(extension, body, HttpStatusCode.of(StatusCode.OK))
                .getResponse();
    }
}
