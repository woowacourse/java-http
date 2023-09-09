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
    public HttpResponse service(HttpRequest request) throws Exception {
        try {
            if (request.isGET()) {
                return doGet(request);

            }
            if (request.isPOST()) {
                return doPost(request);
            }
            return null;
        } catch (Exception e) {
            return handle500();
        }
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return null;
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return null;
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

    private HttpResponse handle500() throws IOException, URISyntaxException {
        String responseBody = getHtmlFile(getClass().getResource("/static/500.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.INTERNAL_SERVER_ERROR, responseHeader, responseBody);
    }
}

