package org.apache.coyote.http11;

import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws Exception;
}
