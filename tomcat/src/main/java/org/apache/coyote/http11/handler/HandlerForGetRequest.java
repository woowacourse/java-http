package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerForGetRequest extends Handler {

    static final HandlerForGetRequest HANDLER_FOR_GET_REQUEST = new HandlerForGetRequest();

    HandlerForGetRequest() {
    }

    @Override
    public HandlerResult handle(HttpRequest request) throws IOException {
        final String uri = request.getUri();
        return new HandlerResult(HttpStatusCode.OK, createResponseHeader(uri),
                getResponseBody(uri));
    }

    public Map<String, String> createResponseHeader(final String requestUri) throws IOException {
        final Map<String, String> header = new LinkedHashMap<>();
        header.put("Content-Type", getContentType(requestUri) + ";charset=utf-8");

        final String responseBody = getResponseBody(requestUri);

        header.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return header;
    }

    String getContentType(final String uri) {
        if (uri.contains(".css")) {
            return "text/css";
        }

        if (uri.contains(".js")) {
            return "text/javascript";
        }

        return "text/html";
    }

    String getResponseBody(String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }

        final URL resourceUrl = HandlerForGetRequest.class.getClassLoader().getResource("static" + requestUri);
        return readContext(resourceUrl);
    }

    private String readContext(final URL resourceUrl) throws IOException {
        final File resourceFile = new File(resourceUrl.getFile());
        final Path resourcePath = resourceFile.toPath();
        final byte[] resourceContents = Files.readAllBytes(resourcePath);
        return new String(resourceContents);
    }
}
