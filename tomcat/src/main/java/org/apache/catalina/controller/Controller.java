package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws IOException;
}
