package servlet;

import servlet.request.HttpRequest;
import servlet.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
