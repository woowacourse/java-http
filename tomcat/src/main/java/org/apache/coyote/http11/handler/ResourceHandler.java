package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseLine;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class ResourceHandler implements Handler {

    private final HttpRequest httpRequest;

    public ResourceHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String path = httpRequest.getRequestTarget();
        ContentType contentType = ContentType.from(path);
        ResponseLine responseLine = ResponseLine.of(ResponseStatusCode.OK);
        String responseBody = FileReader.getFile(path, getClass());
        HttpResponse httpResponse = HttpResponse.of(responseLine, contentType, responseBody);
        return httpResponse.getResponse();
    }
}
