package org.apache.catalina.servlets;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;

public abstract class AbstractController implements Controller {

    public static final String PARAM_DELIMITER = "&";
    public static final String KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
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
            response.addHeader("Content-Type", resource.getContentType().getValue());
            response.setBody(resource.getData());
        } catch (IllegalArgumentException e) {
            Resource resource = resourceLocator.locate("/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.addHeader("Content-Type", resource.getContentType().getValue());
            response.setBody(resource.getData());
        }
    }

    protected Map<String, String> parseFormPayload(HttpRequest request, HttpResponse response) {
        HttpHeaders headers = request.getHeaders();
        String contentType = headers.get("Content-Type");
        if (!contentType.equals("application/x-www-form-urlencoded")) {
            response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            return null;
        }
        String body = request.getBody();
        Map<String, String> data = new HashMap<>();
        String[] components = body.split(PARAM_DELIMITER);
        for (String component : components) {
            String[] keyVal = component.split(KEY_VALUE_DELIMITER);
            String key = keyVal[KEY_INDEX];
            String value = keyVal[VALUE_INDEX];
            data.put(key, value);
        }
        return data;
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
