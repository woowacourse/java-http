package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class IndexHandler implements Handler {

    public IndexHandler(final HttpRequest httpRequest) {
    }

    @Override
    public String getResponse() {
        String responseBody = FileReader.getFile("/index.html", getClass());

        HttpResponse httpResponse = HttpResponse.of(ContentType.HTML, responseBody);
        return httpResponse.getResponse();
    }
}
