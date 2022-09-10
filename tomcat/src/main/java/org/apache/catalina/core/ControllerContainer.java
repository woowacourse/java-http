package org.apache.catalina.core;

import java.io.IOException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class ControllerContainer {

    private final static RequestMapping REQUEST_MAPPING = new RequestMapping();

    public ControllerContainer(final Configuration configuration) {
        configuration.addController(REQUEST_MAPPING);
    }

    public static void service(final HttpRequest request, final HttpResponse response) throws IOException {
        REQUEST_MAPPING.service(request, response);
    }
}
