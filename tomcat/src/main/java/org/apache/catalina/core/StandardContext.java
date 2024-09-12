package org.apache.catalina.core;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.RequestMapping;

public class StandardContext {

    public static void processRequest(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = RequestMapping.getController(request.getURI());
            controller.service(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
