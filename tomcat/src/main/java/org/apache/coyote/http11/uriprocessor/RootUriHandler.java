package org.apache.coyote.http11.uriprocessor;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;

public class RootUriHandler implements UriHandler {

    @Override
    public boolean canHandle(String uri) {
        return "/".equals(uri);
    }

    @Override
    public UriResponse getResponse(String path, Map<String, Object> parameters) {
        return new UriResponse("Hello world!", ContentType.HTML.getValue());
    }
}
