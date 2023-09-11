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
    public void service(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        URL filePathUrl = getClass().getResource("/static" + request.getPath());
        if (filePathUrl == null) {
            handle404(response);
            return;
        }
        String responseBody = readHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.OK, responseHeader, responseBody);
    }

    private void handle404(HttpResponse response) throws IOException, URISyntaxException {
        URL filePathUrl = getClass().getResource("/static/404.html");

        String responseBody = readHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.NOT_FOUND, responseHeader, responseBody);
    }
}
