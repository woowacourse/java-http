package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws IOException;
}
