package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        try {
            if (request.isGET()) {
                doGet(request, response);
            }
            if (request.isPOST()) {
                doPost(request, response);
            }
        } catch (Exception e) {
            handle500(response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected String getHtmlFile(URL filePathUrl) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(Objects.requireNonNull(filePathUrl).toURI());
        return new String(Files.readAllBytes(filePath));
    }

    protected String getContentType(final String accept, final String uri) {
        final String[] tokens = uri.split("\\.");
        if ((tokens.length >= 1 && tokens[tokens.length - 1].equals("css")) || (accept != null && accept
                .contains("text/css"))) {
            return HttpResponseHeader.TEXT_CSS_CHARSET_UTF_8;
        }
        return HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8;
    }

    private void handle500(HttpResponse response) throws IOException, URISyntaxException {
        String responseBody = getHtmlFile(getClass().getResource("/static/500.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length), null, null);
        response.updateResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, responseHeader, responseBody);
    }
}

