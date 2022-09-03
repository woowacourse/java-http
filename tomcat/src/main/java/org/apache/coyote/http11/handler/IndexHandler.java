package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.HttpResponseLine;
import org.apache.coyote.http11.model.response.HttpStatusCode;

public class IndexHandler implements Handler {

    public static final String INDEX_RESOURCE_PATH = "/index.html";

    public IndexHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        String responseBody = FileReader.getFile(INDEX_RESOURCE_PATH, getClass());
        HttpResponseLine responseLine = HttpResponseLine.of(HttpStatusCode.OK);
        HttpResponse httpResponse = HttpResponse.of(responseLine, ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }
}
