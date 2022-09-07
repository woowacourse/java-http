package org.apache.controller;

import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
