package org.apache.coyote.http11.handler.FileHandler;

import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class FileHandler implements Handler {

    private static final Pattern FILE_URI_PATTERN = Pattern.compile("/.+\\.(html|css|js|ico)");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.GET, FILE_URI_PATTERN);
    }

    public FileHandlerResponse getResponse(HttpRequest httpRequest) {
        return new FileHandlerResponse(HttpStatus.OK, httpRequest.getPath());
    }
}
