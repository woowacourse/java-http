package org.apache.coyote;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public abstract class AbstractController implements Controller{

    @Override
    public void service(Request request, Response response) throws Exception {
        String method = request.getHttpMethod();
        if ("GET".equalsIgnoreCase(method)) {
            doGet(request, response);
        } else if ("POST".equalsIgnoreCase(method)) {
            doPost(request, response);
        } else {
            response.setHttpStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED);
            response.addHeader("Content-Length", "0");
        }
    }

    protected void doPost(Request request, Response response) throws Exception{};
    protected void doGet(Request request, Response response) throws Exception{};
}
