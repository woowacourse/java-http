package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class Controller {

    public abstract HttpResponse process(HttpRequest request);
}
