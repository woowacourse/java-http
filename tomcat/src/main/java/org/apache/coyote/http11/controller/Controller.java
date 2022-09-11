package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest request) throws IOException;
}
