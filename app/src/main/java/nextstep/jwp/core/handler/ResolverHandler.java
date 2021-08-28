package nextstep.jwp.core.handler;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public abstract class ResolverHandler implements Handler {

    @Override
    public String doRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        final ModelAndView modelAndView = doRequestWithResolving(httpRequest, httpResponse);
        return modelAndView.getViewAsString();
    }

    protected abstract ModelAndView doRequestWithResolving(HttpRequest httpRequest,
            HttpResponse httpResponse);
}
