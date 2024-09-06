package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest httpRequest);
}
