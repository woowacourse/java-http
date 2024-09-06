package servlet.handler;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;

public class ResourceHandler implements Handler {

    @Override
    public void handleRequest(Request request, Response response) {
        response.configureViewAndStatus(request.getPath(), StatusCode.OK);
    }
}
