package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.controllerResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public interface Controller {

    ApplicationResponse service(HttpRequest httpRequest);
}
