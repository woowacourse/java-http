package org.apache.catalina.connector;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class CoyoteAdapter implements Adapter {

    private final RequestMapping requestMapping = new RequestMapping();

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
