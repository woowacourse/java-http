package servlet.handler;

import org.apache.coyote.request.Request;
import servlet.ResponseAndView;

@FunctionalInterface
public interface Handler {

    ResponseAndView handlerRequest(Request request);
}
