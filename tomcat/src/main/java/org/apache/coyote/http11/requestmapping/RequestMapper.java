package org.apache.coyote.http11.requestmapping;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public interface RequestMapper {

    Controller mapController(HttpRequest httpRequest);
}
