package org.apache.coyote.http11.urihandler;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;
import org.apache.coyote.http11.request.HttpRequest;

public class FileUriHandler extends DefaultUriHandler {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("/.+\\.(html|css|js|ico)");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(FILE_URI_PATTERN);
    }

    @Override
    public UriResponse getResponse(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.getPath();
        String responseBody = getResponseBody("static" + path);
        String contentType = ContentType.of(getFileType(path));

        return new UriResponse(responseBody, contentType);
    }

    private String getFileType(String uri) {
        return uri.split("\\.")[1];
    }
}
