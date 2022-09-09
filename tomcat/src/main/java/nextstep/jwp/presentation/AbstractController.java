package nextstep.jwp.presentation;

import http.header.ContentType;
import http.header.HttpHeaders;
import http.request.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.response.HttpStatus;
import http.support.ResourcesUtil;
import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.container.handler.RequestHandler;

abstract class AbstractController implements RequestHandler {

    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_EXTENSION = ".html";

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws Exception {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return doGet(httpRequest);
        }
        if (httpMethod == HttpMethod.POST) {
            return doPost(httpRequest);
        }
        throw new MethodNotAllowedException();
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        throw new MethodNotAllowedException();
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new MethodNotAllowedException();
    }

    protected String readResource(final HttpRequest httpRequest) throws IOException {
        String resourceUrl = addExtension(httpRequest.getUrl());
        return ResourcesUtil.readResource(STATIC_PATH + resourceUrl);
    }

    private String addExtension(final String url) {
        String extension = ResourcesUtil.parseExtension(url);
        if (extension.isBlank()) {
            return url + DEFAULT_EXTENSION;
        }
        return url;
    }

    protected HttpResponse redirect(final String redirectUrl) {
        HttpHeaders responseHeaders = HttpHeaders.createEmpty();
        responseHeaders.add("Location", redirectUrl);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }

    protected HttpHeaders setResponseHeaders(final HttpRequest httpRequest, final String responseBody) {
        HttpHeaders responseHeaders = HttpHeaders.createEmpty();
        ContentType responseContentType = httpRequest.getAcceptContentType();
        responseHeaders.add("Content-Type", responseContentType.getValue() + ";charset=utf-8");
        if (!responseBody.isBlank()) {
            responseHeaders.add("Content-Length", String.valueOf(responseBody.getBytes().length));
        }
        return responseHeaders;
    }
}
