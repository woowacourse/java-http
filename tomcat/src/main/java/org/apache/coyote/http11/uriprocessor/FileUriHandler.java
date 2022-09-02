package org.apache.coyote.http11.uriprocessor;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;

public class FileUriHandler extends DefaultUriHandler {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile(".+\\.(html|css|js|ico)");
    @Override
    public boolean canHandle(String uri) {
        Matcher matcher = FILE_URI_PATTERN.matcher(uri);
        return matcher.matches();
    }

    @Override
    public UriResponse getResponse(String path, Map<String, Object> parameters) throws IOException {
        String responseBody = getResponseBody("static" + path);
        String contentType = ContentType.of(getFileType(path));

        return new UriResponse(responseBody, contentType);
    }

    private static String getFileType(String uri) {
        return uri.split("\\.")[1];
    }
}
