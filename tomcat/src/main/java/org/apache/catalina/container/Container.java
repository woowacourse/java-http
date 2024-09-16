package org.apache.catalina.container;

import jakarta.controller.ResourceFinder;
import jakarta.controller.StaticResourceController;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpSessionWrapper;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.Http11Processor;

import java.net.Socket;

public class Container {

    private final ResourceProcessor resourceProcessor;
    private final HttpSessionWrapper httpSessionWrapper;

    public Container(RequestMapping requestMapping, HttpSessionWrapper httpSessionWrapper) {
        this.resourceProcessor = new ResourceProcessor(
                requestMapping,
                new ResourceFinder(),
                new StaticResourceController()
        );
        this.httpSessionWrapper = httpSessionWrapper;
    }

    public Runnable acceptConnection(Socket connection) {
        return () -> {
            Processor processor = new Http11Processor(connection, httpSessionWrapper);

            try (connection) {
                HttpRequest httpRequest = processor.getRequest();
                HttpResponse response = resourceProcessor.processResponse(httpRequest);
                processor.process(response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
