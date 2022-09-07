package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class Controller {

    public abstract HttpResponse getResponse(HttpRequest httpRequest) throws IOException;
}
