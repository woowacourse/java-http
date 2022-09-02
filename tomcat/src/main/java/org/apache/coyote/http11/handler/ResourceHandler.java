package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class ResourceHandler implements Handler{

    private final HttpRequest httpRequest;

    public ResourceHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String path = httpRequest.getPath();
        String responseBody = FileReader.getFile(path, getClass());

        ContentType contentType = ContentType.from(path);
        HttpResponse httpResponse = HttpResponse.of(contentType, responseBody);
        return httpResponse.getResponse();
    }
}
