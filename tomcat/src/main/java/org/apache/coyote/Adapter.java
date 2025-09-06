package org.apache.coyote;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Adapter {

    void service(Http11Request request, Http11Response response);

}
