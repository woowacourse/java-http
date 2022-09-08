package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.support.ResourcesUtil;

abstract class AbstractController implements Controller {

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
