package org.apache.front;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.exception.PageRedirectException;

public class Proxy {

    private static final StaticMapping staticMapping = new StaticMapping();

    private static final RequestMapping requestMapping = new RequestMapping();

    public void process(final HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isStatic()) {
            doProcess(staticMapping, httpRequest, httpResponse);
            return;
        }
        doProcess(requestMapping, httpRequest, httpResponse);
    }

    private void doProcess(final Controller controller, final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            controller.service(httpRequest, httpResponse);
        } catch (PageRedirectException pageRedirectException) {
            pageRedirectException.setResponse();
        }
    }
}
