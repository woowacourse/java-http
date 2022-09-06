package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.MessageBody;

public class HomeController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        return HttpResponse.ok(ContentType.TEXT_HTML, new MessageBody("Hello world!"));
    }
}
