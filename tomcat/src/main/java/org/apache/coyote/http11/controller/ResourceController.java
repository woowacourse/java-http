package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.util.FileReader;

public class ResourceController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        String uri = httpRequest.getRequestLine()
                .getRequestTarget()
                .getUri();
        String responseBody = FileReader.read(uri);
        return HttpResponse.ok(ContentType.from(uri), new MessageBody(responseBody));
    }
}
