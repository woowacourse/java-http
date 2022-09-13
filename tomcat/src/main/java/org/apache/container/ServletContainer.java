package org.apache.container;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.presentation.Controller;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;
import org.apache.coyote.http11.http11response.ResponseManager;

public class ServletContainer {

    private RequestMapping requestMapping;

    public ServletContainer() {
        this.requestMapping = new RequestMapping();
    }

    public void process(Http11Request request, Http11Response response) {
        try {
            Controller controller = requestMapping.findController(request);
            if (controller == null) {
                ResponseManager.resourceResponseComponent(response, request.getUri(), StatusCode.OK);
                return;
            }
            controller.service(request, response);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }
}
