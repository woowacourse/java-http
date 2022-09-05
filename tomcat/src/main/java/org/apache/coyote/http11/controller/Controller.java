package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse1;

public abstract class Controller {

    public abstract HttpResponse1 getResponse(HttpRequest httpRequest) throws IOException;
}
