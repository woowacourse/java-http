package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseLine;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class IndexHandler implements Handler {

    public static final String INDEX_RESOURCE_PATH = "/index.html";

    public IndexHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        String responseBody = FileReader.getFile(INDEX_RESOURCE_PATH, getClass());
        ResponseLine responseLine = ResponseLine.of(ResponseStatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(responseLine, ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }
}
