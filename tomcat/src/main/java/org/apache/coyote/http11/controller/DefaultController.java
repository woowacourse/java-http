package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class DefaultController extends AbstractController {
    @Override
    public boolean isSupported(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        URL filePathUrl = getClass().getResource("/static" + request.getPath());
        if (filePathUrl == null) {
            return handle404();
        }
        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
    }

    private HttpResponse handle404() throws IOException, URISyntaxException {
        URL filePathUrl = getClass().getResource("/static/404.html");

        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.NOT_FOUND, responseHeader, responseBody);
    }
}
