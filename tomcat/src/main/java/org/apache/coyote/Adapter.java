package org.apache.coyote;

import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpResponse;
import org.apache.coyote.util.response.HttpStatus;

public interface Adapter {

    void service(HttpRequest request, HttpResponse response) throws Exception;

    void handleError(HttpResponse response, HttpStatus status);
}
