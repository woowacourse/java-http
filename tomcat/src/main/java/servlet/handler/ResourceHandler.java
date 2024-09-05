package servlet.handler;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.StatusCode;
import servlet.ResponseAndView;

public class ResourceHandler implements Handler {

    @Override
    public ResponseAndView handlerRequest(Request request) {
        return new ResponseAndView(request.getPath(), StatusCode.OK);
    }
}
