package org.apache.coyote.http11.request.mapping.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    HttpResponse handle(final HttpRequest httpRequest);
}
