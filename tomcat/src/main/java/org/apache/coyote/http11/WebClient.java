package org.apache.coyote.http11;

import java.text.MessageFormat;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpRequestUri;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.apache.coyote.support.Controller;
import org.apache.coyote.util.FileUtils;

public class WebClient {

    public static final String INDEX_BODY = "Hello world!";

    public HttpResponse request(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }
        throw new MethodNotAllowedException(MessageFormat.format("not allowed : {0}", httpRequest.getMethod()));
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        if (httpRequest.isIndex()) {
            return getForObject(httpRequest, ContentType.TEXT_HTML_CHARSET_UTF_8, INDEX_BODY);
        }

        if (httpRequest.isQueryString()) {
            Controller controller = new Controller(httpRequest);
            return getForObject(controller.execute(httpRequest.getUri()));
        }

        String responseBody = FileUtils.readAllBytes(httpRequest.getUri().getValue());
        return getForObject(httpRequest, responseBody);
    }

    private HttpResponse getForObject(final String body) {
        HttpRequestUri httpRequestUri = new HttpRequestUri(body);
        String responseBody = FileUtils.readAllBytes(httpRequestUri.getValue());
        return HttpResponse.builder()
                .body(responseBody)
                .version(HttpVersion.HTTP_1_1)
                .status(HttpStatus.OK.getValue())
                .contentType(ContentType.TEXT_HTML_CHARSET_UTF_8.getValue())
                .contentLength(WebClient.INDEX_BODY.getBytes().length)
                .build();
    }

    private HttpResponse getForObject(final HttpRequest httpRequest, final String responseBody) {
        return HttpResponse.builder()
                .body(responseBody)
                .version(httpRequest.getVersion())
                .status(HttpStatus.OK.getValue())
                .contentType(httpRequest.getUri().getContentType().getValue())
                .contentLength(responseBody.getBytes().length)
                .build();
    }

    private HttpResponse getForObject(final HttpRequest httpRequest, final ContentType contentType, String body) {
        return HttpResponse.builder()
                .body(body)
                .version(httpRequest.getVersion())
                .status(HttpStatus.OK.getValue())
                .contentType(contentType.getValue())
                .contentLength(body.getBytes().length)
                .build();
    }
}
