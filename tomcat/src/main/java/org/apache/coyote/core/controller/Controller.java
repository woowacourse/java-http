package org.apache.coyote.core.controller;


import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws IOException, UncheckedServletException;
}
