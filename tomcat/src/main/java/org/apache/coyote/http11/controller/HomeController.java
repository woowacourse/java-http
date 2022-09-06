package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.MessageBody;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        MessageBody messageBody = new MessageBody("Hello world!");
        return HttpResponse.ok(ContentType.TEXT_HTML, messageBody);
    }
}
