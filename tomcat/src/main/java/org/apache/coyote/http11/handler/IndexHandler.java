package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class IndexHandler implements Handler {

    private static final String INDEX_RESOURCE_PATH = "/index.html";

    public IndexHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        String responseBody = FileReader.getFile(INDEX_RESOURCE_PATH, getClass());
        HttpResponse httpResponse = HttpResponse.of(ResponseStatusCode.OK, ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }
}
