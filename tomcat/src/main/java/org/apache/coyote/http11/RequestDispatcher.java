package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestDispatcher {

    public static void dispatchRequest(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = RequestMapping.getController(request);

        if (controller != null) {
            controller.service(request, response);
            return;
        }

        if (ContentMimeType.isEndsWithExtension(request.getPath())) {
            new StaticResourceController().service(request, response);
            return;
        }
        response.sendRedirect("/404.html");
    }
}
