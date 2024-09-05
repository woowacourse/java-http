package servlet.handler;

import org.apache.coyote.request.Request;

public interface HandlerMapping {

    Handler getHandler(Request request);
}
