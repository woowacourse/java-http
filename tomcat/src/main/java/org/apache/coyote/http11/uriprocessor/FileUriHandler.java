package org.apache.coyote.http11.uriprocessor;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;

public class FileUriHandler extends DefaultUriHandler {

    @Override
    public UriResponse getResponse(String uri) throws IOException {
        String responseBody = getResponseBody("static" + uri);
        String contentType = ContentType.of(getFileType(uri));

        return new UriResponse(responseBody, contentType);
    }

    private static String getFileType(String uri) {
        return uri.split("\\.")[1];
    }
}
