package org.apache.coyote;

import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public interface ServletContainer {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
