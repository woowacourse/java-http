package org.apache.catalina;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) {
        switch (request.method()) {
            case "GET" -> toGet(request, response);
            case "POST" -> toPost(request, response);
            default -> throw new IllegalArgumentException("Unsupported method: " + request.method());
        }
    }
}
