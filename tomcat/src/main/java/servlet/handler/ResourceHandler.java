package servlet.handler;

import servlet.http.request.Request;
import servlet.http.response.Response;
import servlet.http.StatusCode;

public class ResourceHandler implements Handler {

    @Override
    public void handleRequest(Request request, Response response) {
        response.configureViewAndStatus(request.getPath(), StatusCode.OK);
    }
}
