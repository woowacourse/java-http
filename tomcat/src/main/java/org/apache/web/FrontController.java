package org.apache.web;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.StartLine;

public class FrontController {

    private final ControllerMapping controllerMapping = new ControllerMapping();

    public Http11Response service(final Http11Request request) {
        //요청에 해당하는 Controller를 찾아야함.
        final StartLine startLine = request.getStartLine();
        final Controller controller = controllerMapping.findController(startLine);

        if (controller == null) {
            return Http11Response.notFound("text/html;charset=utf-8", "Not Found");
        }
        return controller.control(request);
    }
}
