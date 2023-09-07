package org.apache.catalina.servlets;

import common.FileReader;
import nextstep.mvc.ResponseWriter;
import org.apache.catalina.servlets.config.ServletMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class Container {

    private Container() {

    }

    public static void process(HttpRequest request, HttpResponse response) {
        Servlet servlet = ServletMapping.getServlet(request);
        if (servlet == null) {
            if (FileReader.findResource(request.getPath()) == null) {
                ResponseWriter.view(response, HttpStatus.NOT_FOUND, "/404.html");
                return;
            }
            ResponseWriter.view(response, HttpStatus.OK, request.getPath());
            return;
        }
        servlet.service(request, response);
    }
}
