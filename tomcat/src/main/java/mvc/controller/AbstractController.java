package mvc.controller;

import mvc.controller.exception.UnsupportedHttpMethodException;
import servlet.request.HttpRequest;
import org.apache.coyote.http.util.HttpMethod;
import servlet.Controller;
import servlet.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isGetMethod(request)) {
            doGet(request, response);
            return ;
        }
        if (isPostMethod(request)) {
            doPost(request, response);
            return ;
        }

        throw new UnsupportedHttpMethodException();
    }

    private boolean isGetMethod(final HttpRequest request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isPostMethod(final HttpRequest request) {
        return request.matchesByMethod(HttpMethod.POST);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedHttpMethodException();
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedHttpMethodException();
    }
}
