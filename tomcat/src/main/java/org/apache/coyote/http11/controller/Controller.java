package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

  HttpResponse service(HttpRequest request) throws IOException;
}
