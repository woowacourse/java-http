package org.apache.catalina;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Controller {

    void service(Http11Request request, Http11Response response);

    void toGet(Http11Request request, Http11Response response);

    void toPost(Http11Request request, Http11Response response);
}
