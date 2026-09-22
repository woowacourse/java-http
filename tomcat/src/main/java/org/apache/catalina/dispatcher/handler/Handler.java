package org.apache.catalina.dispatcher.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Handler {

    boolean supports(HttpRequest request);

    String handle(HttpRequest request, HttpResponse response) throws IOException;

}
