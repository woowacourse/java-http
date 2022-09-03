package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.HttpResponseLine;
import org.apache.coyote.http11.model.response.HttpStatusCode;

public class ResourceHandler implements Handler {

    private final HttpRequest httpRequest;

    public ResourceHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String path = httpRequest.getRequestTarget();
        ContentType contentType = ContentType.from(path);
        HttpResponseLine responseLine = HttpResponseLine.of(HttpStatusCode.OK);
        String responseBody = FileReader.getFile(path, getClass());
        HttpResponse httpResponse = HttpResponse.of(responseLine, contentType, responseBody);
        return httpResponse.getResponse();
    }
}
