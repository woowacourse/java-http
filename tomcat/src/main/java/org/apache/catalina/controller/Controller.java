package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    String doService(HttpRequest request, HttpResponse response) throws IOException;

}
