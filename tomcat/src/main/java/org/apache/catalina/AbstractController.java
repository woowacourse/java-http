package org.apache.catalina;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
        }
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
        }
    }

    abstract void doGet(Http11Request request, Http11Response response) throws Exception;

    abstract void doPost(Http11Request request, Http11Response response) throws Exception;
}
