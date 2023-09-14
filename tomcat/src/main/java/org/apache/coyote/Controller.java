package org.apache.coyote;

import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
