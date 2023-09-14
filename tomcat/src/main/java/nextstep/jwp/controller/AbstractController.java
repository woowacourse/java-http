package nextstep.jwp.controller;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    public final ClassLoader classLoader = getClass().getClassLoader();

    public static final String RESOURCE_PATH = "static";
    public static final String UNAUTHORIZED_PAGE = "/401.html";
    public static final String NOT_FOUND_PAGE = "/404.html";
    public static final String HTTP_FOUND = "Found";
    public static final String INDEX_PAGE = "/index.html";

    public void service(final HttpRequest httpRequest, final Http11Response httpResponse) {
        final String httpMethod = httpRequest.getRequestLine().getHttpMethod();

        if (httpMethod.equals("POST")) {
            doPost(httpRequest, httpResponse);
        } else if (httpMethod.equals("GET")) {
            doGet(httpRequest, httpResponse);
        } else {
            redirectNotFoundPage(httpResponse);
        }
    }

    private void redirectNotFoundPage(final Http11Response httpResponse) {
        final String resourcePath = RESOURCE_PATH + NOT_FOUND_PAGE;
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(404);
        httpResponse.setStatusMessage("Not Found");
    }

    abstract void doGet(HttpRequest request, Http11Response response);

    abstract void doPost(HttpRequest request, Http11Response response);
}
