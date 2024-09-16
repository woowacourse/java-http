package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class FrontController {

    private FrontController() {
    }

    public static void service(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = RequestMapping.getController(request);
        controller.service(request, response);
    }
}
