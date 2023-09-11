package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Controller {

    Logger log = LoggerFactory.getLogger(Controller.class);

    HttpResponse service(HttpRequest httpRequest, HttpResponse httpResponse);
}
