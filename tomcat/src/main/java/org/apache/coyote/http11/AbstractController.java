package org.apache.coyote.http11;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Resource;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;

public abstract class AbstractController implements Controller {

    protected final ResourceLocator resourceLocator;

    protected AbstractController(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    @Override
    public final void service(HttpRequest request, HttpResponse response) {
        switch (request.getMethod()) {
            case GET:
                doGet(request, response);
                break;
            case HEAD:
                doHead(request, response);
                break;
            case POST:
                doPost(request, response);
                break;
            case PUT:
                doPut(request, response);
                break;
            case PATCH:
                doPatch(request, response);
                break;
            case DELETE:
                doDelete(request, response);
                break;
            case OPTIONS:
                doOptions(request, response);
                break;
            case TRACE:
                doTrace(request, response);
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP Method : " + request.getMethod());
        }
    }

    protected final void doHtmlResponse(HttpResponse response, String path) {
        try {
            Resource resource = resourceLocator.locate(path);
            response.setStatus(HttpStatus.OK);
            response.addHeader("Content-Type", resource.getMimeType().getValue());
            response.setBody(resource.getData());
        } catch (IllegalArgumentException e) {
            Resource resource = resourceLocator.locate("/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.addHeader("Content-Type", resource.getMimeType().getValue());
            response.setBody(resource.getData());
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doHead(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPut(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPatch(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doDelete(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doOptions(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doTrace(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

}
