package servlet;

import org.apache.coyote.http.request.HttpRequest;
import servlet.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
