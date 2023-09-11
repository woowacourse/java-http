package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.exception.UnauthorizeException;
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

    public static final int EXTENSION_TOKENS_MIN_SIZE = 1;

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        try {
            handleRequest(request, response);
        } catch (UnauthorizeException e) {
            handle401(request, response);
        } catch (Exception e) {
            handle500(response);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isGET()) {
            doGet(request, response);
        }
        if (request.isPOST()) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected String readHtmlFile(URL filePathUrl) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(Objects.requireNonNull(filePathUrl).toURI());
        return new String(Files.readAllBytes(filePath));
    }

    protected String readContentType(final String accept, final String uri) {
        final String[] tokens = uri.split("\\.");
        if (isExtensionCss(tokens) || isAcceptCss(accept)) {
            return HttpResponseHeader.TEXT_CSS_CHARSET_UTF_8;
        }
        return HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8;
    }

    private boolean isExtensionCss(String[] tokens) {
        return tokens.length >= EXTENSION_TOKENS_MIN_SIZE && tokens[tokens.length - 1].equals("css");
    }

    private boolean isAcceptCss(String accept) {
        return accept != null && accept.contains("text/css");
    }

    private void handle401(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        String responseBody = readHtmlFile(getClass().getResource("/static/401.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.UNAUTHORIZATION, responseHeader, responseBody);
    }

    private void handle500(HttpResponse response) throws IOException, URISyntaxException {
        String responseBody = readHtmlFile(getClass().getResource("/static/500.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                HttpResponseHeader.TEXT_HTML_CHARSET_UTF_8,
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, responseHeader, responseBody);
    }
}

