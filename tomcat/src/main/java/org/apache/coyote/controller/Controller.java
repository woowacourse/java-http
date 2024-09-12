package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class Controller {

    public abstract HttpResponse process(HttpRequest request);
}
