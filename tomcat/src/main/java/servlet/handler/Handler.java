package servlet.handler;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

@FunctionalInterface
public interface Handler {

    void handleRequest(Request request, Response response);
}
