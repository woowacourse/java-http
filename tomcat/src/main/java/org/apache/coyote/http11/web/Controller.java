package org.apache.coyote.http11.web;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    View handleRequest(HttpRequest request, HttpResponse response);
}
