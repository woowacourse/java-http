package nextstep.jwp.core.mvc;

import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public abstract class ResolverHandler implements Handler {

    @Override
    public String doRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        final ModelAndView modelAndView = doRequestWithResolving(httpRequest, httpResponse);
        if(modelAndView.isRedirect()) {
            return "";
        }
        return modelAndView.getViewAsString();
    }

    protected abstract ModelAndView doRequestWithResolving(HttpRequest httpRequest,
            HttpResponse httpResponse);
}
