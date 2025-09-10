package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public interface Controller {

    ApplicationResponse service(HttpRequest httpRequest);
}
