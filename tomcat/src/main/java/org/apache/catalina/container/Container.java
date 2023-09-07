package org.apache.catalina.container;

import common.FileReader;
import nextstep.mvc.ResponseWriter;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.config.ControllerMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class Container {

    private Container() {
    }

    public static void process(HttpRequest request, HttpResponse response) {
        Controller controller = ControllerMapping.getController(request);
        if (controller == null) {
            if (FileReader.findResource(request.getPath()) == null) {
                ResponseWriter.view(response, HttpStatus.NOT_FOUND, "/404.html");
                return;
            }
            ResponseWriter.view(response, HttpStatus.OK, request.getPath());
            return;
        }
        controller.service(request, response);
    }
}
