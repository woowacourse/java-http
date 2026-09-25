package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Adapter {

    HttpResponse service(HttpRequest request) throws Exception;
}
