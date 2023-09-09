package org.apache.controller;

import java.util.Set;
import org.apache.controller.ControllerException.ControllerHttpMethodException;
import org.apache.controller.ControllerException.ControllerNotImplementMethodException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public abstract class AbstractController implements Controller {

    protected static final String CONTENT_LENGTH = "Content-Length";
    protected static final String CONTENT_TYPE = "Content-Type";

    protected static final String INDEX_PATH = "/index.html";
    protected static final String UNAUTHORIZED_PATH = "/401.html";

    private final String url;
    private final Set<HttpMethod> availableHttpMethods;

    protected AbstractController(String url, Set<HttpMethod> availableHttpMethods) {
        this.url = url;
        this.availableHttpMethods = availableHttpMethods;
    }

    @Override
    public boolean support(Request request) {
        return request.isSamePath(url) && availableHttpMethods.contains(request.getHttpMethod());
    }

    @Override
    public void service(Request request, Response response) {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new ControllerHttpMethodException(request.getHttpMethod());
    }

    protected void doGet(Request request, Response response) {
        if (availableHttpMethods.contains(HttpMethod.GET)) {
            throw new ControllerNotImplementMethodException(HttpMethod.GET);
        }
        throw new ControllerHttpMethodException(HttpMethod.GET);
    }

    protected void doPost(Request request, Response response) {
        if (availableHttpMethods.contains(HttpMethod.POST)) {
            throw new ControllerNotImplementMethodException(HttpMethod.POST);
        }
        throw new ControllerHttpMethodException(HttpMethod.POST);
    }
}
