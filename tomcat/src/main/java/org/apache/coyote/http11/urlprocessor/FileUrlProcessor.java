package org.apache.coyote.http11.urlprocessor;

import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UrlResponse;

public class FileUrlProcessor extends DefaultUrlProcessor {

    @Override
    public UrlResponse getResponse(String url) throws IOException {
        String responseBody = getResponseBody("static" + url);
        String contentType = ContentType.of(getFileType(url));

        return new UrlResponse(responseBody, contentType);
    }

    private static String getFileType(String url) {
        return url.split("\\.")[1];
    }
}
