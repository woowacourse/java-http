package org.apache.coyote.http11;

import com.techcourse.controller.model.Controller;
import org.apache.coyote.http11.request.mapper.RequestMapping;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.response.model.HttpResponse;

public class RequestProcessor {

    private final RequestMapping requestMapping;

    public RequestProcessor() {
        this.requestMapping = new RequestMapping();
    }

    public HttpResponse process(HttpRequest httpRequest) {
        Controller controller = requestMapping.findController(httpRequest);
        return controller.service(httpRequest);
    }
}
