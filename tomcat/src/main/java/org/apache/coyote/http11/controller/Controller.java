package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httpresponse.HttpResponse;

public interface Controller {

    HttpResponse process(String uri);
}
