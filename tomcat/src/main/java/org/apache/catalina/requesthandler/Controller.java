package org.apache.catalina.requesthandler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request, HttpResponse response) throws Exception;
}
