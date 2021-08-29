package nextstep.jwp.core.mvc;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.core.exception.StatusException;
import nextstep.jwp.core.mvc.mapping.HandlerMapping;
import nextstep.jwp.core.mvc.mapping.MethodHandlerMapping;
import nextstep.jwp.webserver.exception.PageNotFoundException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.ContentType;
import nextstep.jwp.webserver.response.HttpResponse;
import nextstep.jwp.webserver.response.StatusCode;

public class FrontHandler {

    private final HandlerMapping handlerMapping;
    private final ApplicationContext applicationContext;

    public FrontHandler(String basePackage) {
        this.applicationContext = new DefaultApplicationContext(basePackage);
        this.handlerMapping = new MethodHandlerMapping(applicationContext);
    }

    public HttpResponse getResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            final Handler handler = handlerMapping.findHandler(httpRequest);
            if (handler == null) {
                return resourceHandle(httpRequest, httpResponse);
            }

            final String content = handler.doRequest(httpRequest, httpResponse);
            httpResponse.addHeader("Content-Length", String.valueOf(content.getBytes().length));
            httpResponse.addBody(content);
        } catch (StatusException e) {
            addExceptionPage(httpResponse, e.getStatusCode(), e.getPage());
        } catch (Exception e) {
            addExceptionPage(httpResponse, StatusCode.SERVER_ERROR, "500.html");
        }
        return httpResponse;
    }

    private void addExceptionPage(HttpResponse httpResponse, StatusCode statusCode, String page) {
        String path = "static/" + page;
        httpResponse.addStatus(statusCode);
        httpResponse.addPage(path);
    }

    private HttpResponse resourceHandle(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String file = httpRequest.httpUrl();
        httpResponse.addPage("static" + file);
        httpResponse.addHeader("Content-Type", ContentType.contentType(file));
        httpResponse.addStatus(StatusCode.OK);
        return httpResponse;
    }
}
