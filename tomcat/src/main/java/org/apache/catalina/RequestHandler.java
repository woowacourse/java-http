package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface RequestHandler {

    HttpResponse service(HttpRequest httpRequest) throws Exception;
}
