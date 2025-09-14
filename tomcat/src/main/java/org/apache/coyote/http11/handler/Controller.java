package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.applicationRequest.ApplicationRequest;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;

public interface Controller {

    ApplicationResponse service(ApplicationRequest applicationRequest);
}
