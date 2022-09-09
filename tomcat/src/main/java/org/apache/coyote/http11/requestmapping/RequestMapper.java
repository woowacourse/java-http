package org.apache.coyote.http11.requestmapping;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public interface RequestMapper {

    Controller mapController(HttpRequest httpRequest);
}
