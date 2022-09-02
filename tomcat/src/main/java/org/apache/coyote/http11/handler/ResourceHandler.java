package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class ResourceHandler implements Handler {

    private final HttpRequest httpRequest;

    public ResourceHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String path = httpRequest.getPath();
        ContentType contentType = ContentType.from(path);

        String responseBody = FileReader.getFile(path, getClass());
        HttpResponse httpResponse = HttpResponse.of(contentType, responseBody);
        return httpResponse.getResponse();
    }
}
