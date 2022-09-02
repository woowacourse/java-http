package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.ResourceUtil;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class FileHandle implements Handler{

    private final HttpRequest httpRequest;

    public FileHandle(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String path = httpRequest.getPath();
        ContentType contentType = ContentType.of(path);

        String responseBody = ResourceUtil.getResponseBody(path, getClass());
        HttpResponse httpResponse = HttpResponse.from(contentType, responseBody);
        return httpResponse.getResponse();
    }
}
