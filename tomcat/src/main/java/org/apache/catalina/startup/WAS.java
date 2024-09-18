package org.apache.catalina.startup;

import com.techcourse.controller.HttpController;
import com.techcourse.controller.ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class WAS {
    private final Server tomcat;
    private final Controllers controllers;

    public WAS(Server tomcat, Controllers controllers) {
        this.tomcat = tomcat;
        this.controllers = controllers;
    }

    public void start() {
        tomcat.start();
    }

    public static HttpResponse dispatch(HttpRequest request) throws Exception {
        HttpResponse response = new HttpResponse();

        if (controllers.contains(request.getLocation())) {
            HttpController targetController = controllers.get(request.getLocation());
            targetController.service(request, response);
            return response;
        }

        String body = new ResourceFinder(request.getLocation(), request.getExtension()).getStaticResource(
                response);
        response.setBody(body);
        return response;
    }
}
