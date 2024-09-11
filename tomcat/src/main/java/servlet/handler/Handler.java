package servlet.handler;

import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;

@FunctionalInterface
public interface Handler {

    void handleRequest(Request request, Response response);
}
