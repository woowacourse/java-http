package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.MessageBody;

public class HomeHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        return HttpResponse.ok(ContentType.TEXT_HTML, new MessageBody("Hello world!"));
    }
}
