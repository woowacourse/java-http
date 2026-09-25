package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws IOException;
}
