package org.apache.catalina.container;

import com.techcourse.handler.HelloController;
import com.techcourse.handler.LoginController;
import com.techcourse.handler.NotFoundController;
import com.techcourse.handler.RegisterController;
import jakarta.controller.Controller;
import jakarta.controller.ResourceFinder;
import jakarta.controller.StaticResourceController;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpSessionWrapper;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Processor;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Container {

    private final ResourceProcessor resourceProcessor;
    private final HttpSessionWrapper httpSessionWrapper;

    public Container(HttpSessionWrapper httpSessionWrapper) {
        this.resourceProcessor = new ResourceProcessor(
                createRequestMapping(),
                new ResourceFinder(),
                new StaticResourceController());
        this.httpSessionWrapper = httpSessionWrapper;
    }

    private RequestMapping createRequestMapping() {
        Map<String, Controller> mapping = new HashMap<>();
        mapping.put("/", new HelloController());
        mapping.put("/login", new LoginController());
        mapping.put("/register", new RegisterController());

        return new RequestMapping(mapping, new NotFoundController());
    }

    public Runnable acceptConnection(Socket connection) {
        return () -> {
            Processor processor = new Http11Processor(connection, httpSessionWrapper);

            try {
                HttpRequest httpRequest = processor.getRequest();
                HttpResponse response = resourceProcessor.processResponse(httpRequest);
                processor.process(response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
