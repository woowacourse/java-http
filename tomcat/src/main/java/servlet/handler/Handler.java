package servlet.handler;

import servlet.http.request.Request;
import servlet.http.response.Response;

@FunctionalInterface
public interface Handler {

    void handleRequest(Request request, Response response);
}
