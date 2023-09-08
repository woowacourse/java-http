package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.controllermapping.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.response.HttpResponse;

@RequestMapping(regex = ".*\\.(html|css|js)$")
public class StaticResourceController extends AbstractController {

    public static final String RESOURCE_PATH_PREFIX = "static/";

    private FileHandler fileHandler = new FileHandler();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getEndPoint().endsWith(".html")) {
            response.setCharSet("utf-8");
            response.setContentType("text/html");
            response.setResponseStatus("200");
            response.setResponseBody(fileHandler.readFromResourcePath(RESOURCE_PATH_PREFIX + request.getFullUri()));
            response.flush();
        }
        if (request.getEndPoint().endsWith(".css")) {
            response.setCharSet("utf-8");
            response.setContentType("text/css");
            response.setResponseStatus("200");
            response.setResponseBody(fileHandler.readFromResourcePath(RESOURCE_PATH_PREFIX + request.getFullUri()));
            response.flush();
        }
        if (request.getEndPoint().endsWith(".js")) {
            response.setCharSet("utf-8");
            response.setContentType("text/javascript");
            response.setResponseStatus("200");
            response.setResponseBody(fileHandler.readFromResourcePath(RESOURCE_PATH_PREFIX + request.getFullUri()));
            response.flush();
        }
    }
}
