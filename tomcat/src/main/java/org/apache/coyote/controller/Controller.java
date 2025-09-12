package org.apache.coyote.controller;

import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
